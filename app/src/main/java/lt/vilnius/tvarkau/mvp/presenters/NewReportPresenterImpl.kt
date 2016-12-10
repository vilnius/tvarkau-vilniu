package lt.vilnius.tvarkau.mvp.presenters

import lt.vilnius.tvarkau.fragments.NewReportFragment
import lt.vilnius.tvarkau.mvp.interactors.NewReportInteractor
import lt.vilnius.tvarkau.mvp.views.NewReportView
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import rx.Scheduler
import rx.Subscription

/**
 * @author Martynas Jurkus
 */
class NewReportPresenterImpl(
        private val interactor: NewReportInteractor,
        private val view: NewReportView,
        private val uiScheduler: Scheduler
) : NewReportPresenter {

    private var subscription: Subscription? = null

    override fun onAttach() {
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun initWithReportType(reportType: String) {
        if (reportType == NewReportFragment.TRAFFIC_VIOLATIONS) {
            view.showPersonalDataFields()
        }
    }

    override fun submitProblem(validator: FieldAwareValidator<NewReportData>) {
        validator.toSingle()
                .flatMap { interactor.submitReport(it) }
                .doOnSubscribe { view.showProgress() }
                .doOnUnsubscribe { view.hideProgress() }
                .observeOn(uiScheduler)
                .subscribe({
                    handleSuccess(it)
                }, {
                    handleError(it)
                })
                .apply { subscription = this }
    }

    private fun handleSuccess(reportId: String) {
        view.showSuccess(reportId)
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is FieldAwareValidator.ValidationException -> view.showValidationError(error)
            else -> view.showError(error)
        }
    }
}