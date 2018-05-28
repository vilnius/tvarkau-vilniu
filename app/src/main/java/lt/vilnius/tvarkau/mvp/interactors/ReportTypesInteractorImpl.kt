package lt.vilnius.tvarkau.mvp.interactors

import io.reactivex.Scheduler
import io.reactivex.Single
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportTypesRequest

/**
 * @author Martynas Jurkus
 */
class ReportTypesInteractorImpl(
        val api: LegacyApiService,
        val ioScheduler: Scheduler
) : ReportTypesInteractor {

    private var entryCache = listOf<String>()

    override fun getReportTypes(): Single<List<String>> {
        return Single.concat(
                Single.just(entryCache),
                api.getProblemTypes(GetReportTypesRequest())
                        .map { it.result }
                        .doOnSuccess {
                            if (it == null || it.isEmpty()) {
                                throw IllegalStateException("No report types to display")
                            }
                        }
                        .doOnSuccess { entryCache = it }
        )
                .filter { it.isNotEmpty() }
                .firstOrError()
                .subscribeOn(ioScheduler)
    }
}