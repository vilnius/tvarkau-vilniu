package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.paging.PagedList
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.repository.Listing
import lt.vilnius.tvarkau.repository.ReportsRepository
import javax.inject.Inject

class MyReportListViewModel @Inject constructor(
    private val repository: ReportsRepository
) : BaseViewModel() {

    private val reportsResult = MutableLiveData<Listing<ReportEntity>>()

    val refreshState = switchMap(reportsResult, Listing<ReportEntity>::refreshState)!!
    val networkState = switchMap(reportsResult, Listing<ReportEntity>::networkState)!!
    val reports: LiveData<PagedList<ReportEntity>> = switchMap(reportsResult) { it.pagedList }

    fun refresh() {
        reportsResult.value?.refresh?.invoke()
    }

    fun getReports() {
        reportsResult.value = repository.reportsForCurrentUser()
    }
}
