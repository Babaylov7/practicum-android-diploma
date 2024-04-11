package ru.practicum.android.diploma.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.models.ResponseStatus
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.domain.search.VacancyInteractor
import ru.practicum.android.diploma.ui.main.model.MainFragmentStatus
import ru.practicum.android.diploma.util.Utilities

class MainViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val utilities: Utilities
) : ViewModel() {
    private var requestText = ""
    private var job: Job? = null
    private var list = ArrayList<Vacancy>()
    private var foundVacancies: Int = 0
    private var maxPages: Int = 0

    private val _listOfVacancies: MutableLiveData<MainFragmentStatus> = MutableLiveData(MainFragmentStatus.Default)
    val listOfVacancies: LiveData<MainFragmentStatus> = _listOfVacancies

    private var _page: MutableLiveData<Int> = MutableLiveData(0)
    val page: LiveData<Int> = _page

    fun onDestroy() {
        job?.cancel()
    }

//    fun getCurrentPage(): Int {
//        return page
//    }

    fun getMaxPages(): Int {
        return maxPages
    }
    
    fun getFoundVacancies(): Int {
        return foundVacancies
    }

    fun changeRequestText(text: String) {
        requestText = text
    }

    fun getRequestText(): String {
        return requestText
    }

    fun searchDebounce() {
        job?.cancel()
        job = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            search()
        }
    }

    fun clickDebounce(): Boolean {
        return utilities.eventDebounce(viewModelScope, CLICK_DEBOUNCE_DELAY_MILLIS)
    }

    fun scrollDebounce(): Boolean {
        return utilities.eventDebounce(viewModelScope, CLICK_DEBOUNCE_DELAY_MILLIS)
    }

    fun installPage(oldRequest: Boolean) {
        if (oldRequest) {
            _page.value = _page.value!! + 1
        } else {
            _page.value = 0
        }
    }

    fun search() {
        _listOfVacancies.postValue(MainFragmentStatus.Loading)
        sendRequest()
    }

    private fun sendRequest() {
        viewModelScope.launch {
            vacancyInteractor
                .searchVacancy(requestText, _page.value!!)
                .collect { result ->
                    when (result.responseStatus) {
                        ResponseStatus.OK -> {
                            if (_page.value!! == 0) {
                                list.clear()
                                list.addAll(result.results)
                                foundVacancies = result.found
                                _listOfVacancies.postValue(
                                    MainFragmentStatus.ListOfVacancies(
                                        result.results
                                    )
                                )
                                maxPages = result.pages
                            } else {
                                list.addAll(result.results)
                                _listOfVacancies.postValue(MainFragmentStatus.ListOfVacancies(list))
                            }
                        }

                        ResponseStatus.BAD -> {
                            list.clear()
                            _listOfVacancies.postValue(MainFragmentStatus.Bad)
                        }

                        ResponseStatus.DEFAULT -> {
                            _listOfVacancies.postValue(MainFragmentStatus.Default)
                        }

                        ResponseStatus.NO_CONNECTION -> {
                            _listOfVacancies.postValue(MainFragmentStatus.NoConnection)
                        }

                        else -> {
                        }
                    }
                }
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}
