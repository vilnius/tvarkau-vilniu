package lt.vilnius.tvarkau.fragments.interactors

import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportListRequest
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.RefreshMapEvent
import lt.vilnius.tvarkau.extensions.emptyToNull
import lt.vilnius.tvarkau.prefs.StringPreference
import lt.vilnius.tvarkau.rx.RxBus
import rx.Scheduler
import rx.Single

class MultipleReportsMapInteractorImpl(
        private val api: LegacyApiService,
        private val ioScheduler: Scheduler,
        private val reportType: StringPreference,
        private val reportStatus: StringPreference,
        private val allReportTypes: String
) : MultipleReportsMapInteractor {

    private val cachedReports = mutableListOf<Problem>()

    init {
        RxBus.observable
                .filter { it is RefreshMapEvent }
                .subscribe({
                    cachedReports.clear()
                })
    }

    override fun getReports(): Single<List<Problem>> {
        return Single.concat(
                Single.just(cachedReports),
                newRequest()
        ).first { it.isNotEmpty() }
                .toSingle()
                .doOnSuccess { reports ->
                    if (reports.isEmpty()) {
                        throw IllegalStateException("Empty problem list returned")
                    }
                }
                .subscribeOn(ioScheduler)
    }

    private fun newRequest(): Single<List<Problem>> {
        val mappedStatus = reportStatus.get().emptyToNull()

        val mappedType = when (reportType.get()) {
            allReportTypes -> null
            else -> reportType.get().emptyToNull()
        }

        val params = GetProblemsParams.Builder()
                .setStart(0)
                .setLimit(PROBLEM_COUNT_LIMIT_IN_MAP)
                .setStatusFilter(mappedStatus)
                .setTypeFilter(mappedType)
                .create()

        val request = GetReportListRequest(params)

        return api.getProblems(request)
                .toSingle()
                .map { it.result }
                .doOnSuccess {
                    cachedReports.clear()
                    cachedReports.addAll(it)
                }
    }

    companion object {
        private const val PROBLEM_COUNT_LIMIT_IN_MAP = 200
    }
}