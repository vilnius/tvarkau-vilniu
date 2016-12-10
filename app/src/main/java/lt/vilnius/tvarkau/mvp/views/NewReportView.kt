package lt.vilnius.tvarkau.mvp.views

import lt.vilnius.tvarkau.utils.FieldAwareValidator

/**
 * @author Martynas Jurkus
 */
interface NewReportView {

    fun showProgress()

    fun hideProgress()

    fun showPersonalDataFields()

    fun showValidationError(error: FieldAwareValidator.ValidationException)

    fun showError(error: Throwable)

    fun showSuccess(reportId: String)
}