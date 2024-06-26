package ru.practicum.android.diploma.domain.db

import ru.practicum.android.diploma.domain.models.vacancy.VacancyDetails

sealed interface FavoriteVacancyState {

    data class SuccessfulRequest(val vacancy: VacancyDetails) : FavoriteVacancyState

    data class FailedRequest(val error: String) : FavoriteVacancyState

}
