package lt.vilnius.tvarkau.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import io.reactivex.BackpressureStrategy
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import lt.vilnius.tvarkau.api.ReportsResponse
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.dagger.DbScheduler
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.entity.Report
import lt.vilnius.tvarkau.entity.ReportEntity
import javax.inject.Inject


class ReportsRepository @Inject constructor(
    private val reportsDao: ReportsDao,
    private val reportTypeRepository: ReportTypeRepository,
    private val reportStatusRepository: ReportStatusRepository,
    private val api: TvarkauMiestaApi,
    @UiScheduler
    private val uiScheduler: Scheduler,
    @DbScheduler
    private val dbScheduler: Scheduler
) {

    private val boundaryCallback by lazy {
        ReportsBoundaryCallback(
            api = api,
            handleResponse = this::insertResultIntoDb
        )
    }

    private fun refresh(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        api.getReports(page = 1)
            .doOnSuccess { reportsDao.deleteAll() }
            .doOnSuccess(this::insertResultIntoDb)
            .doOnSuccess { boundaryCallback.onRefresh() }
            .observeOn(uiScheduler)
            .subscribeBy(
                onSuccess = { networkState.postValue(NetworkState.LOADED) },
                onError = { networkState.value = NetworkState.error(it.message) }
            )

        return networkState
    }

    fun getReportById(reportId: Int): Single<ReportEntity> {
        return reportsDao.getById(reportId)
            .map { mapReportEntity(it) }
            .subscribeOn(dbScheduler)
    }

    fun reports(initialLoadKey: Int?): Listing<ReportEntity> {
        val dataSourceFactory = reportsDao.reportsDataSourceFactory()
            .map(::mapReportEntity)

        val builder = RxPagedListBuilder(
            dataSourceFactory,
            PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(60)
                .setPageSize(20)
                .setPrefetchDistance(60)
                .build()
        )
            .setInitialLoadKey(initialLoadKey)
            .setBoundaryCallback(boundaryCallback)
            .setNotifyScheduler(uiScheduler)

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        return Listing(
            pagedList = builder.buildFlowable(BackpressureStrategy.LATEST),
            networkState = MutableLiveData(), //TODO provide network state to display network errors if needed
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    private fun mapReportEntity(report: Report): ReportEntity {
        return ReportEntity(
            id = report.id,
            refNo = report.refNo,
            reportType = reportTypeRepository.getById(report.reportTypeId),
            lat = report.lat,
            lng = report.lng,
            userId = report.userId,
            reportStatus = reportStatusRepository.getById(report.reportStatusId),
            description = report.description,
            answer = report.answer,
            licensePlateNo = report.licensePlateNo,
            registeredAt = report.registeredAt,
            completedAt = report.completedAt
        )
    }

    private fun insertResultIntoDb(response: ReportsResponse?) {
        if (response == null || response.reports.isEmpty()) return
        reportsDao.insertAll(response.reports)
    }
}
