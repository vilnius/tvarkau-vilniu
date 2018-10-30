package lt.vilnius.tvarkau.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import io.reactivex.rxkotlin.subscribeBy
import lt.vilnius.tvarkau.api.ReportsResponse
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.entity.ReportEntity
import timber.log.Timber


class ReportsBoundaryCallback(
    private val api: TvarkauMiestaApi,
    private val handleResponse: (ReportsResponse?) -> Unit,
    private val additionalParams: Map<String, String> = emptyMap()
) : PagedList.BoundaryCallback<ReportEntity>() {

    private var nextPage = 0

    val networkState = MutableLiveData<NetworkState>()

    fun onRefresh() {
        nextPage = 0
    }

    override fun onZeroItemsLoaded() = doRequest()

    override fun onItemAtEndLoaded(itemAtEnd: ReportEntity) = doRequest()

    private fun doRequest() {
        api.getReports(buildRequestParams())
            .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
            .doFinally { networkState.postValue(NetworkState.LOADED) }
            .doOnSuccess { nextPage++ }
            .subscribeBy(
                onSuccess = { handleResponse(it) },
                onError = { Timber.e(it) }
            )
    }

    private fun buildRequestParams(): Map<String, String> {
        return mapOf(
            "per_page" to 20.toString(),
            "page" to nextPage.toString()
        ) + additionalParams
    }

    override fun onItemAtFrontLoaded(itemAtFront: ReportEntity) {
        // ignored, since we only ever append to what's in the DB
    }
}
