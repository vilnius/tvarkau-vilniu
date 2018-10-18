package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractor
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import lt.vilnius.tvarkau.repository.ReportsRepository
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import java.io.File
import javax.inject.Inject

class NewReportViewModel @Inject constructor(
    @UiScheduler
    private val uiScheduler: Scheduler,
    private val personalDataInteractor: PersonalDataInteractor,
    private val analytics: Analytics,
    private val repository: ReportsRepository
) : BaseViewModel() {

    private val _personalData = MutableLiveData<Profile>()
    val personalData: LiveData<Profile>
        get() = _personalData

    private val _validationError = SingleLiveEvent<FieldAwareValidator.ValidationException>()
    val validationError: LiveData<FieldAwareValidator.ValidationException>
        get() = _validationError

    private val _submittedReport = MutableLiveData<ReportEntity>()
    val submittedReport: LiveData<ReportEntity>
        get() = _submittedReport

    fun initWith(reportType: ReportType) {
        if (reportType.isParkingViolation) {
            val profile = if (!personalDataInteractor.isUserAnonymous()) {
                personalDataInteractor.getPersonalData()
            } else {
                null
            }

            _personalData.value = profile
        }
    }

    /**
     * Missing support from backend for:
     * - personal code field
     * - email
     * - phone
     * - full name
     *
     * All fields should be available from user's personal data after sign-in via Vilniaus Vartai
     */
    fun submit(validator: FieldAwareValidator<NewReportData>) {
        validator.toSingle()
            .flatMap { repository.submitReport(it) }
            .observeOn(uiScheduler)
            .bindProgress()
            .subscribeBy(
                onSuccess = this::handleSuccess,
                onError = this::handleError
            )
    }

    private fun handleSuccess(report: ReportEntity) {
        analytics.trackReportRegistration(
            reportType = report.reportType.title,
            photoCount = 0 //TODO set photo count
        )

        _submittedReport.value = report
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is FieldAwareValidator.ValidationException -> {
                _validationError.value = error
                analytics.trackReportValidation(error.message ?: "no message")
            }
            else -> _errorEvents.value = error
        }
    }

    fun onImagesPicked(imageFiles: List<File>) {
        //TODO implement image upload with new API
    }
}
