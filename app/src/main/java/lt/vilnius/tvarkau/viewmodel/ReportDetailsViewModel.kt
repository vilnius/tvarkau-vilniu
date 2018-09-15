package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.repository.ReportsRepository
import javax.inject.Inject

class ReportDetailsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository,
    @UiScheduler
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _report = MutableLiveData<ReportEntity>()
    val report: LiveData<ReportEntity>
        get() = _report

    fun initWith(reportId: Int) {
        reportsRepository.getReportById(reportId)
            .observeOn(uiScheduler)
            .subscribeBy(
                onSuccess = ::handleSuccess,
                onError = ::handleError
            ).bind()
    }

    private fun handleSuccess(report: ReportEntity) {
        _report.value = report
    }

    private fun handleError(throwable: Throwable) {
        _errorEvents.value = throwable
    }
}
