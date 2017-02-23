package lt.vilnius.tvarkau.fragments.views

import lt.vilnius.tvarkau.entity.Problem

/**
 * @author Martynas Jurkus
 */
interface MultipleProblemsMapView {

    fun addMarkers(reports: List<Problem>)

    fun showProgress()

    fun hideProgress()

    fun showError()
}