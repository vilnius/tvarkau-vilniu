package lt.vilnius.tvarkau.mvp.presenters

import lt.vilnius.tvarkau.utils.FieldAwareValidator

/**
 * @author Martynas Jurkus
 */
interface NewReportPresenter {

    fun onAttach()

    fun onDetach()

    fun initWithReportType(reportType: String)

    fun submitProblem(validator: FieldAwareValidator<NewReportData>)
}