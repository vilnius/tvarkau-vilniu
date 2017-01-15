package lt.vilnius.tvarkau.fragments.interactors

import lt.vilnius.tvarkau.backend.ApiMethod
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.entity.Problem
import rx.Scheduler
import rx.Single

/**
 * @author Martynas Jurkus
 */
class AllReportListInteractor(
        private val legacyApiService: LegacyApiService,
        private val ioScheduler: Scheduler
) : ReportListInteractor {

    override fun getProblems(page: Int): Single<List<Problem>> {

        val params = GetProblemsParams.Builder()
                .setStart(page * PROBLEM_COUNT_LIMIT_PER_PAGE)
                .setLimit(PROBLEM_COUNT_LIMIT_PER_PAGE)
                .create()

        val request = ApiRequest(ApiMethod.GET_PROBLEMS, params)

        return legacyApiService.getProblems(request)
                .subscribeOn(ioScheduler)
                .map { it.result }
                .toSingle()
    }

    companion object {
        const val PROBLEM_COUNT_LIMIT_PER_PAGE = 20
    }
}