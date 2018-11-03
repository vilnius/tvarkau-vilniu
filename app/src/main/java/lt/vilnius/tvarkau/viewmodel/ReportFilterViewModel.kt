package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Scheduler
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.dagger.DbScheduler
import lt.vilnius.tvarkau.entity.ReportStatus
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.events_listeners.RefreshReportFilterEvent
import lt.vilnius.tvarkau.prefs.AppPreferences
import lt.vilnius.tvarkau.repository.ReportStatusRepository
import lt.vilnius.tvarkau.repository.ReportTypeRepository
import lt.vilnius.tvarkau.rx.RxBus
import javax.inject.Inject


class ReportFilterViewModel @Inject constructor(
    private val reportTypeRepository: ReportTypeRepository,
    private val reportStatusRepository: ReportStatusRepository,
    private val appPreferences: AppPreferences,
    private val analytics: Analytics,
    @DbScheduler
    private val dbScheduler: Scheduler
) : BaseViewModel() {

    private val _reportTypes = MutableLiveData<List<ReportTypeViewEntity>>()
    val reportTypes: LiveData<List<ReportTypeViewEntity>>
        get() = _reportTypes

    private val _reportStatuses = MutableLiveData<List<ReportStatusViewEntity>>()
    val reportStatuses: LiveData<List<ReportStatusViewEntity>>
        get() = _reportStatuses

    private var targetMap = false

    fun initWith(allReportsTitle: String, targetMap: Boolean) {
        this.targetMap = targetMap

        val allReportsType = ReportType(id = 0, title = allReportsTitle)
        initReportTypes(allReportsType)
        initReportStatuses()
    }

    fun onReportTypeSelected(reportType: ReportType) {
        _reportTypes.value = _reportTypes.value!!.map { it.copy(selected = it.reportType == reportType) }
    }

    fun onReportStatusSelected(reportStatus: ReportStatus) {
        _reportStatuses.value = _reportStatuses.value!!.map {
            if (it.selected && it.reportStatus == reportStatus) {
                it.copy(selected = false)
            } else {
                it.copy(selected = it.reportStatus == reportStatus)
            }
        }
    }

    fun onSubmit() {
        val selectedType = _reportTypes.value!!.first { it.selected }.reportType
        val selectedStatus = _reportStatuses.value!!.firstOrNull { it.selected }?.reportStatus

        val targetName = if (targetMap) {
            appPreferences.reportTypeSelectedFilter.set(selectedType.id)
            appPreferences.reportStatusSelectedListFilter.set(selectedStatus?.id ?: 0)
            "map"
        } else {
            appPreferences.reportTypeSelectedListFilter.set(selectedType.id)
            appPreferences.reportStatusSelectedListFilter.set(selectedStatus?.id ?: 0)
            "list"
        }

        analytics.trackApplyReportFilter(selectedStatus?.title.orEmpty(), selectedType.title, targetName)
        RxBus.publish(RefreshReportFilterEvent())
    }

    private fun initReportTypes(allReportsType: ReportType) {
        val previouslySelectedId = previouslySelectedReportTypeId()
        dbScheduler.createWorker().schedule {
            val result = listOf(allReportsType) + reportTypeRepository.getReportTypes()
            _reportTypes.postValue(result.map { ReportTypeViewEntity(it, selected = it.id == previouslySelectedId) })
        }
    }

    private fun initReportStatuses() {
        val statusId = getSelectedReportStatusId()

        dbScheduler.createWorker().schedule {
            val result = reportStatusRepository.getAll().map { ReportStatusViewEntity(it, it.id == statusId) }
            _reportStatuses.postValue(result)
        }
    }

    private fun previouslySelectedReportTypeId(): Int {
        return if (targetMap) {
            appPreferences.reportTypeSelectedFilter.get()
        } else {
            appPreferences.reportTypeSelectedListFilter.get()
        }
    }

    private fun getSelectedReportStatusId(): Int {
        return if (targetMap) {
            appPreferences.reportStatusSelectedFilter.get()
        } else {
            appPreferences.reportStatusSelectedListFilter.get()
        }
    }

    data class ReportTypeViewEntity(
        val reportType: ReportType,
        val selected: Boolean
    )

    data class ReportStatusViewEntity(
        val reportStatus: ReportStatus,
        val selected: Boolean
    )
}
