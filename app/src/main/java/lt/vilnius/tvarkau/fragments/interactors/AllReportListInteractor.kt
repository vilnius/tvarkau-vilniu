package lt.vilnius.tvarkau.fragments.interactors

import com.vinted.preferx.StringPreference
import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportListRequest
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.extensions.emptyToNull
import rx.Scheduler
import rx.Single

/**
 * @author Martynas Jurkus
 */
class AllReportListInteractor(
        private val legacyApiService: LegacyApiService,
        private val ioScheduler: Scheduler,
        private val reportType: StringPreference,
        private val reportStatus: StringPreference,
        private val allReportTypes: String
) : ReportListInteractor {

    override fun getProblems(page: Int): Single<List<Problem>> {

        val mappedStatus = reportStatus.get().emptyToNull()

        val mappedType = when (reportType.get()) {
            allReportTypes -> null
            else -> reportType.get().emptyToNull()
        }

        val params = GetProblemsParams.Builder()
                .setStart(page * PROBLEM_COUNT_LIMIT_PER_PAGE)
                .setLimit(PROBLEM_COUNT_LIMIT_PER_PAGE)
                .setStatusFilter(mappedStatus)
                .setTypeFilter(mappedType)
                .create()

        return legacyApiService.getProblems(GetReportListRequest(params))
                .subscribeOn(ioScheduler)
                .map { it.result ?: emptyList() }
                .toSingle()
    }

    companion object {
        const val PROBLEM_COUNT_LIMIT_PER_PAGE = 20
    }
}
