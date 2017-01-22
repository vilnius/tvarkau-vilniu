package lt.vilnius.tvarkau.mvp.views

import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import java.io.File

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

    fun fillReportDateTime(dateTime: String)

    fun displayImages(imageFiles: List<File>)
}