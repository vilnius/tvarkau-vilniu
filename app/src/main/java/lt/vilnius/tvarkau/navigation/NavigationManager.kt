package lt.vilnius.tvarkau.navigation

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.fragments.*


class NavigationManager(private val activity: AppCompatActivity, private val executor: FragmentTransactionExecutor) {

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
            NavigationManager.TabItem.SETTINGS -> executor.replaceWithClearTop(SettingsFragment.newInstance())
        }
    }

    fun navigateToReportsFilter() {
        executor.replaceWithVerticalAnimation(ReportFilterFragment.newInstance(ReportFilterFragment.TARGET_LIST), true)
    }

    fun navigateToNewReport() {
        val intent = Intent(activity, ReportRegistrationActivity::class.java)
        activity.startActivityForResult(intent, ActivityConstants.REQUEST_CODE_NEW_REPORT)
    }


}