package lt.vilnius.tvarkau.navigation

import lt.vilnius.tvarkau.entity.Problem


interface NavigationManager {

    enum class TabItem {
        REPORTS_LIST,
        MY_REPORTS_LIST,
        REPORTS_MAP,
        SETTINGS
    }

    fun navigateToMenuItem(menuItem: TabItem)

    fun navigateToReportsFilter()

    fun navigateToNewReport()

    fun onBackPressed(): Boolean

    fun showReportsImportDialog()

    fun navigateToProfileEditActivity()

    fun navigateToProblemDetailActivity(problem: Problem)
}