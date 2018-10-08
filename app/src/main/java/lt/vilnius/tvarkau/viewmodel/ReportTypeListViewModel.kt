package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Scheduler
import lt.vilnius.tvarkau.dagger.DbScheduler
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.repository.ReportTypeRepository
import javax.inject.Inject

class ReportTypeListViewModel @Inject constructor(
    @DbScheduler
    private val dbScheduler: Scheduler,
    private val reportTypeRepository: ReportTypeRepository
) : BaseViewModel() {

    private val _reportTypes = MutableLiveData<List<ReportType>>()
     val reportTypes: LiveData<List<ReportType>>
        get() = _reportTypes


    init {
        dbScheduler.createWorker().schedule {
            _reportTypes.postValue(reportTypeRepository.getReportTypes())
        }
    }
}
