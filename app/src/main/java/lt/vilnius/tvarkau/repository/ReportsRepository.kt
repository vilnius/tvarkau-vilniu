package lt.vilnius.tvarkau.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import lt.vilnius.tvarkau.api.NewReportRequest
import lt.vilnius.tvarkau.api.ReportsResponse
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.dagger.DbScheduler
import lt.vilnius.tvarkau.entity.NewReport
import lt.vilnius.tvarkau.entity.Report
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import lt.vilnius.tvarkau.session.UserSession
import timber.log.Timber
import javax.inject.Inject


class ReportsRepository @Inject constructor(
    private val reportsDao: ReportsDao,
    private val reportTypeRepository: ReportTypeRepository,
    private val reportStatusRepository: ReportStatusRepository,
    private val api: TvarkauMiestaApi,
    @DbScheduler
    private val dbScheduler: Scheduler,
    private val userSession: UserSession
) {

    fun getReportById(reportId: Int): Single<ReportEntity> {
        return reportsDao.getById(reportId)
            .map { mapReportEntity(it) }
            .subscribeOn(dbScheduler)
    }

    fun reportsForCurrentUser(): Listing<ReportEntity> {
        val user = userSession.user.value!!
        val boundaryCallback = ReportsBoundaryCallback(
            api = api,
            handleResponse = this::insertResultIntoDb,
            additionalParams = mapOf("user_id" to "${user.id}")
        )

        val dataSourceFactory = reportsDao.reportsForUser(user.id)
            .map(::mapReportEntity)

        return buildReportListing(dataSourceFactory, boundaryCallback)
    }

    fun reports(): Listing<ReportEntity> {
        val boundaryCallback = ReportsBoundaryCallback(
            api = api,
            handleResponse = this::insertResultIntoDb
        )

        val user = userSession.user.value!!
        val dataSourceFactory = reportsDao.reports(user.id)
            .map(::mapReportEntity)

        return buildReportListing(dataSourceFactory, boundaryCallback)
    }

    fun submitReport(reportData: NewReportData): Single<ReportEntity> {
        val newReport = NewReport.from(reportData)

        return api.submitReport(NewReportRequest(newReport))
            .map { it.report }
            .doOnSuccess { reportsDao.insertAll(listOf(it)) }
            .map(::mapReportEntity)
    }

    private fun buildReportListing(
        dataSourceFactory: DataSource.Factory<Int, ReportEntity>,
        boundaryCallback: ReportsBoundaryCallback
    ): Listing<ReportEntity> {
        val builder = LivePagedListBuilder(
            dataSourceFactory,
            PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(60)
                .setPageSize(20)
                .setPrefetchDistance(60)
                .build()
        )
            .setBoundaryCallback(boundaryCallback)

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh(boundaryCallback)
        }

        return Listing(
            pagedList = builder.build(),
            networkState = boundaryCallback.networkState,
            refresh = { refreshTrigger.value = null },
            refreshState = refreshState
        )
    }

    private fun refresh(boundaryCallback: ReportsBoundaryCallback): LiveData<NetworkState> {
        val refreshState = MutableLiveData<NetworkState>()
        refreshState.postValue(NetworkState.LOADING)

        Completable.create {
            boundaryCallback.onRefresh()
            reportsDao.deleteAll()

            it.onComplete()
        }
            .subscribeOn(dbScheduler)
            .doFinally { refreshState.postValue(NetworkState.LOADED) }
            .subscribeBy(onError = { Timber.e(it) })

        return refreshState
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
