package lt.vilnius.tvarkau.fragments.interactors

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import lt.vilnius.tvarkau.backend.GetProblemParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportRequest
import lt.vilnius.tvarkau.entity.Problem
import timber.log.Timber

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
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapMaybe { reportId ->
                    legacyApiService.getProblem(GetReportRequest(GetProblemParams(reportId)))
                            .doOnSuccess {
                                if (it.result == null) myReportsInteractor.removeReportId(reportId)
                            }
                            .flatMapMaybe {
                                if (it.result == null) {
                                    Maybe.empty()
                                } else {
                                    Maybe.just(it.result)
                                }
                            }
                            .doOnError { Timber.e(it) }
                            .onErrorResumeNext(Maybe.empty())
                }
                .toList()
                .map { it.sortedByDescending { it.getEntryDate() } }
                .subscribeOn(ioScheduler)
    }
}