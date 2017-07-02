package lt.vilnius.tvarkau.navigation

import lt.vilnius.tvarkau.fragments.AllReportsListFragment
import lt.vilnius.tvarkau.fragments.MultipleProblemsMapFragment
import lt.vilnius.tvarkau.fragments.MyReportsListFragment
import lt.vilnius.tvarkau.fragments.ReportFilterFragment

class NavigationManager(private val executor: FragmentTransactionExecutor) {

    enum class TabItem {
        REPORTS_LIST,
        MY_REPORTS_LIST,
        REPORTS_MAP,
        SETTINGS
    }

    fun navigateToMenuItem(menuItem: TabItem) {
        when (menuItem) {
            NavigationManager.TabItem.REPORTS_LIST -> executor.replaceWithClearTop(AllReportsListFragment.newInstance())
            NavigationManager.TabItem.MY_REPORTS_LIST -> executor.replaceWithClearTop(MyReportsListFragment.newInstance())
            NavigationManager.TabItem.REPORTS_MAP -> executor.replaceWithClearTop(MultipleProblemsMapFragment.newInstance())
        // TODO migrate settings to fragment
            NavigationManager.TabItem.SETTINGS -> executor.replaceWithClearTop(AllReportsListFragment.newInstance())
        }
    }

    fun navigateToReportsFilter() {
        executor.replaceWithVerticalAnimation(ReportFilterFragment.newInstance(ReportFilterFragment.TARGET_LIST), true)
    }


}