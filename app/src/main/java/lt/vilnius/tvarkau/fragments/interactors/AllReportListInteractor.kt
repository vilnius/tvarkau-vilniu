package lt.vilnius.tvarkau.fragments.interactors

import io.reactivex.Single
import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportListRequest
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.extensions.emptyToNull
import lt.vilnius.tvarkau.prefs.AppPreferences
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */
class AllReportListInteractor @Inject constructor(
        private val legacyApiService: LegacyApiService,
        private val appPreferences: AppPreferences,
        @Named("all_reports")
        private val allReportTypes: String
) {

    fun getProblems(page: Int): Single<List<Problem>> {

        val mappedStatus = appPreferences.reportStatusSelectedListFilter.get().emptyToNull()

        val mappedType = when (appPreferences.reportTypeSelectedListFilter.get()) {
            allReportTypes -> null
            else -> appPreferences.reportTypeSelectedListFilter.get().emptyToNull()
        }

        val params = GetProblemsParams.Builder()
                .setStart(page * PROBLEM_COUNT_LIMIT_PER_PAGE)
                .setLimit(PROBLEM_COUNT_LIMIT_PER_PAGE)
                .setStatusFilter(mappedStatus)
                .setTypeFilter(mappedType)
                .create()

        return legacyApiService.getProblems(GetReportListRequest(params))
                .map { it.result ?: emptyList() }
    }

    companion object {
        const val PROBLEM_COUNT_LIMIT_PER_PAGE = 20
    }
}
