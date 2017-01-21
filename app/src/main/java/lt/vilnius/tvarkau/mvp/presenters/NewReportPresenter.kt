package lt.vilnius.tvarkau.mvp.presenters

import lt.vilnius.tvarkau.utils.FieldAwareValidator
import java.io.File

/**
 * @author Martynas Jurkus
 */
interface NewReportPresenter {

    fun onAttach()

    fun onDetach()

    fun initWithReportType(reportType: String)

    fun submitProblem(validator: FieldAwareValidator<NewReportData>)

    fun onImagesPicked(imageFiles: List<File>)
}