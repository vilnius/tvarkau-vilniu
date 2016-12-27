package lt.vilnius.tvarkau.mvp.presenters

import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.fragments.NewReportFragment
import lt.vilnius.tvarkau.mvp.interactors.NewReportInteractor
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractor
import lt.vilnius.tvarkau.mvp.views.NewReportView
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import org.threeten.bp.LocalDate
import rx.Scheduler
import rx.Subscription

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

    private var subscription: Subscription? = null

    override fun onAttach() {
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun initWithReportType(reportType: String) {
        if (reportType == NewReportFragment.PARKING_VIOLATIONS) {
            val profile = if (!personalDataInteractor.isUserAnonymous()) {
                personalDataInteractor.getPersonalData()
            } else {
                null
            }

            view.showPersonalDataFields(profile)
        }
    }

    override fun submitProblem(validator: FieldAwareValidator<NewReportData>) {
        validator.toSingle()
                .flatMap { interactor.submitReport(it) }
                .doOnSuccess { updatePersonalData(validator.get()) }
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnUnsubscribe { view.hideProgress() }
                .subscribe({
                    handleSuccess(it)
                }, {
                    handleError(it)
                })
                .apply { subscription = this }
    }

    private fun updatePersonalData(reportData: NewReportData) {
        if (reportData.reportType != NewReportFragment.PARKING_VIOLATIONS
                || personalDataInteractor.isUserAnonymous()) {
            return
        }

        personalDataInteractor.storePersonalData(
                Profile(
                        reportData.name,
                        LocalDate.parse(reportData.dateOfBirth),
                        reportData.email,
                        reportData.phone
                ))
    }

    private fun handleSuccess(reportId: String) {
        view.showSuccess(reportId)
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