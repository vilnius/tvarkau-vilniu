package lt.vilnius.tvarkau.fragments.interactors

import lt.vilnius.tvarkau.backend.GetProblemParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportRequest
import lt.vilnius.tvarkau.entity.Problem
import rx.Observable
import rx.Scheduler
import rx.Single

/**
 * @author Martynas Jurkus
 */
class MyReportListInteractor(
        private val legacyApiService: LegacyApiService,
        private val myReportsInteractor: MyReportsInteractor,
        private val ioScheduler: Scheduler
) : ReportListInteractor {

    override fun getProblems(page: Int): Single<List<Problem>> {
        return myReportsInteractor.getReportIds()
                .flatMapObservable { Observable.from(it) }
                .flatMap { reportId ->
                    legacyApiService.getProblem(GetReportRequest(GetProblemParams(reportId)))
                            .map { it.result }
                            .doOnNext { if (it == null) myReportsInteractor.removeReportId(reportId) }
                            .onErrorReturn { null }
                }
                .filter { it != null }
                .toList()
                .map { it.sortedBy { it.getEntryDate() } }
                .subscribeOn(ioScheduler)
                .toSingle()
    }
}