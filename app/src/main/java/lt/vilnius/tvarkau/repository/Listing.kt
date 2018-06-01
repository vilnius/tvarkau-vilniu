package lt.vilnius.tvarkau.repository

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import io.reactivex.Flowable

/**
 * Data class that is necessary for a UI to show a listing and interact w/ the rest of the system
 */
data class Listing<T>(
    val pagedList: Flowable<PagedList<T>>,
    // represents the network request status to show to the user
    val networkState: LiveData<NetworkState>,
    // represents the refresh status to show to the user. Separate from networkState, this
    // value is importantly only when refresh is requested.
    val refreshState: LiveData<NetworkState>,
    val refresh: () -> Unit
)
