package lt.vilnius.tvarkau.mvp.interactors

import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportTypesRequest
import rx.Scheduler
import rx.Single

/**
 * @author Martynas Jurkus
 */
class ReportTypesInteractorImpl(
        val api: LegacyApiService,
        val ioScheduler: Scheduler
) : ReportTypesInteractor {

    private val entryCache = mutableListOf<String>()

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
                        .doOnSuccess { entryCache.addAll(it) }
        )
                .first { it.isNotEmpty() }
                .toSingle()
                .subscribeOn(ioScheduler)
    }
}