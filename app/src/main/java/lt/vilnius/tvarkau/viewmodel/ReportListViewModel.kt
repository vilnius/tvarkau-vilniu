package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.repository.Listing
import lt.vilnius.tvarkau.repository.ReportsRepository
import javax.inject.Inject

class ReportListViewModel @Inject constructor(
    private val repository: ReportsRepository
) : BaseViewModel() {

    private val reportsResult = MutableLiveData<Listing<ReportEntity>>()

    val networkState = switchMap(reportsResult, Listing<ReportEntity>::networkState)!!
    val refreshState = switchMap(reportsResult, Listing<ReportEntity>::refreshState)!!
    val reports = switchMap(reportsResult) { LiveDataReactiveStreams.fromPublisher(it.pagedList) }!!

    fun refresh() {
        reportsResult.value?.refresh?.invoke()
    }

    fun getReports(initialLoadKey: Int?) {
        reportsResult.value = repository.reports(initialLoadKey)
    }
}
