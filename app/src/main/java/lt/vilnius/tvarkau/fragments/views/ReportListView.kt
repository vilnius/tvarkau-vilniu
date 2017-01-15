package lt.vilnius.tvarkau.fragments.views

import lt.vilnius.tvarkau.entity.Problem

/**
 * @author Martynas Jurkus
 */
interface ReportListView {
    fun onReportsLoaded(reports: List<Problem>)
    fun hideLoader()
    fun markLoading(isLoading: Boolean)
    fun showEmptyState()
    fun hideEmptyState()
    fun showNetworkError(lastPage: Int)
    fun showError(error: Throwable)
}