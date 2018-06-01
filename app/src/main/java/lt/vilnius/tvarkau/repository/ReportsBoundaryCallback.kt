package lt.vilnius.tvarkau.repository

import android.arch.paging.PagedList
import io.reactivex.rxkotlin.subscribeBy
import lt.vilnius.tvarkau.api.ReportsResponse
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.entity.ReportEntity
import timber.log.Timber


class ReportsBoundaryCallback(
    private val api: TvarkauMiestaApi,
    private val handleResponse: (ReportsResponse?) -> Unit
) : PagedList.BoundaryCallback<ReportEntity>() {

    private var nextPage = 1

    fun onRefresh() {
        nextPage = 2
    }

    override fun onZeroItemsLoaded() {
        api.getReports(page = nextPage)
            .doOnSuccess { nextPage++ }
            .subscribeBy(
                onSuccess = { handleResponse(it) },
                onError = { Timber.e(it) }
            )
    }

    override fun onItemAtEndLoaded(itemAtEnd: ReportEntity) {
        api.getReports(page = nextPage)
            .doOnSuccess { nextPage++ }
            .subscribeBy(
                onSuccess = { handleResponse(it) },
                onError = { Timber.e(it) }
            )
    }

    override fun onItemAtFrontLoaded(itemAtFront: ReportEntity) {
        // ignored, since we only ever append to what's in the DB
    }
}
