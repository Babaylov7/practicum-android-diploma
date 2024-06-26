package ru.practicum.android.diploma.data.search.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.dto.vacancy.VacancyDto
import ru.practicum.android.diploma.data.dto.vacancy.VacancyResponse
import ru.practicum.android.diploma.data.dto.vacancy.VacancySearchRequest
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.ResponseStatus
import ru.practicum.android.diploma.domain.models.VacancySearchResult
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.domain.search.VacancyRepository
import ru.practicum.android.diploma.util.SalaryInfo

class VacancyRepositoryImpl(
    private val networkClient: NetworkClient,
    private val utils: SalaryInfo
) : VacancyRepository {
    override fun searchVacancy(expression: String, filters: Filters, page: Int): Flow<VacancySearchResult> = flow {
        val response = networkClient.doVacancySearch(
            VacancySearchRequest(formatToQueryMap(expression, filters, page))
        )
        when (response.resultResponse) {
            ResponseStatus.OK -> {
                val vacancies: List<Vacancy> = (response as VacancyResponse).vacancies?.map {
                    formatToVacancy(it)
                } ?: emptyList()
                emit(
                    VacancySearchResult(
                        vacancies,
                        ResponseStatus.OK,
                        response.found ?: 0,
                        response.page ?: 0,
                        response.pages ?: 0
                    )
                )
            }

            ResponseStatus.NO_CONNECTION -> {
                emit(
                    NO_CONNECTION
                )
            }

            ResponseStatus.BAD -> {
                emit(
                    BAD_RESPONSE
                )
            }

            ResponseStatus.DEFAULT -> emit(
                DEFAULT
            )

            else -> {
            }
        }
    }

    private fun formatToVacancy(vacancyDto: VacancyDto): Vacancy {
        return Vacancy(
            vacancyId = vacancyDto.id,
            vacancyName = vacancyDto.name.toString(),
            employer = vacancyDto.employer?.name ?: "",
            areaRegion = vacancyDto.area?.name ?: "",
            salary = utils.getSalaryInfo(
                vacancyDto.salary?.currency ?: "",
                (vacancyDto.salary?.from ?: "").toString(),
                (vacancyDto.salary?.to ?: "").toString()
            ),
            artworkUrl = vacancyDto.employer?.logoUrls?.mediumLogoUrl240 ?: ""
        )
    }

    private fun formatToQueryMap(expression: String, filters: Filters, page: Int): HashMap<String, String> {
        val queryParameters: HashMap<String, String> = HashMap()
        queryParameters["text"] = expression
        queryParameters["page"] = page.toString()
        queryParameters["per_page"] = PER_PAGE_20
        if (filters.countryId.isNotEmpty()) queryParameters["area"] = filters.countryId
        if (filters.regionId.isNotEmpty()) queryParameters["area"] = filters.regionId
        if (filters.salary != 0) queryParameters["salary"] = filters.salary.toString()
        if (filters.industryId.isNotEmpty()) queryParameters["industry"] = filters.industryId
        queryParameters["only_with_salary"] = filters.doNotShowWithoutSalarySetting.toString()
        return queryParameters
    }

    companion object {
        private const val PER_PAGE_20 = "20"

        val BAD_RESPONSE = VacancySearchResult(
            emptyList(),
            ResponseStatus.BAD,
            0,
            0,
            0
        )

        val NO_CONNECTION = VacancySearchResult(
            emptyList(),
            ResponseStatus.NO_CONNECTION,
            0,
            0,
            0
        )

        val DEFAULT = VacancySearchResult(
            emptyList(),
            ResponseStatus.DEFAULT,
            0,
            0,
            0
        )
    }
}
