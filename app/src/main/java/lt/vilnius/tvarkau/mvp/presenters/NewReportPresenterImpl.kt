package lt.vilnius.tvarkau.mvp.presenters

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.fragments.NewReportFragment
import lt.vilnius.tvarkau.mvp.interactors.NewReportInteractor
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractor
import lt.vilnius.tvarkau.mvp.views.NewReportView
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import lt.vilnius.tvarkau.utils.FormatUtils
import lt.vilnius.tvarkau.utils.ImageUtils
import java.io.File
import java.util.*

/**
 * @author Martynas Jurkus
 */
class NewReportPresenterImpl(
        private val interactor: NewReportInteractor,
        private val personalDataInteractor: PersonalDataInteractor,
        private val view: NewReportView,
        private val uiScheduler: Scheduler,
        private val analytics: Analytics
) : NewReportPresenter {

    private var disposable: Disposable? = null
    private lateinit var reportType: String

    override fun onAttach() {
    }

    override fun onDetach() {
        disposable?.dispose()
    }

    override fun initWithReportType(reportType: String) {
        this.reportType = reportType

        if (reportType == NewReportFragment.PARKING_VIOLATIONS) {
            val profile = if (!personalDataInteractor.isUserAnonymous()) {
                personalDataInteractor.getPersonalData()
            } else {
                null
            }

            view.showParkingViolationFields(profile)
        }
    }

    override fun submitProblem(validator: FieldAwareValidator<NewReportData>) {
        validator.toSingle()
                .subscribeOn(Schedulers.io())
                .flatMap { interactor.submitReport(it) }
                .doOnSuccess { updatePersonalData(validator.get()) }
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doFinally { view.hideProgress() }
                .subscribe({
                    handleSuccess()
                }, {
                    handleError(it)
                })
                .apply { disposable = this }
    }

    override fun onImagesPicked(imageFiles: List<File>) {
        if (reportType == NewReportFragment.PARKING_VIOLATIONS) {
            var timeStamp = imageFiles.firstOrNull()
                    ?.let { ImageUtils.getExifTimeStamp(it) }
                    ?.let { FormatUtils.formatExifAsLocalDateTime(it) }

            if (timeStamp == null) {
                timeStamp = imageFiles.firstOrNull()
                        ?.let { FormatUtils.formatLocalDateTime(Date(it.lastModified())) }
            }

            timeStamp?.let { view.fillReportDateTime(it) }
        }

        view.displayImages(imageFiles)
    }

    private fun updatePersonalData(reportData: NewReportData) {
        if (reportData.reportType != NewReportFragment.PARKING_VIOLATIONS
                || personalDataInteractor.isUserAnonymous()) {
            return
        }

        personalDataInteractor.storePersonalData(
                Profile(
                        name = reportData.name,
                        personalCode = reportData.personalCode,
                        email = reportData.email,
                        mobilePhone = reportData.phone
                ))
    }

    private fun handleSuccess() {
        view.showSuccess()
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is FieldAwareValidator.ValidationException -> {
                view.showValidationError(error)
                analytics.trackReportValidation(error.message ?: "no message")
            }
            else -> view.showError(error)
        }
    }
}