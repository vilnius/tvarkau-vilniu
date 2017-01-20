package lt.vilnius.tvarkau.mvp.views

import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.utils.FieldAwareValidator

/**
 * @author Martynas Jurkus
 */
interface NewReportView {

    fun showProgress()

    fun hideProgress()

    fun showParkingViolationFields(profile: Profile?)

    fun showValidationError(error: FieldAwareValidator.ValidationException)

    fun showError(error: Throwable)

    fun showSuccess()
}