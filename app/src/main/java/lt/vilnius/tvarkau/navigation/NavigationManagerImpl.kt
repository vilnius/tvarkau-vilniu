package lt.vilnius.tvarkau.navigation

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import lt.vilnius.tvarkau.ProblemDetailActivity
import lt.vilnius.tvarkau.ProfileEditActivity
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.fragments.*

class NavigationManagerImpl(
        private val activity: AppCompatActivity,
        private val executor: FragmentTransactionExecutor
) : NavigationManager {

    override fun navigateToMenuItem(menuItem: NavigationManager.TabItem) {
        when (menuItem) {
            NavigationManager.TabItem.REPORTS_LIST -> executor.replaceWithClearTop(AllReportsListFragment.newInstance())
            NavigationManager.TabItem.MY_REPORTS_LIST -> executor.replaceWithClearTop(MyReportsListFragment.newInstance())
            NavigationManager.TabItem.REPORTS_MAP -> executor.replaceWithClearTop(MultipleProblemsMapFragment.newInstance())
            NavigationManager.TabItem.SETTINGS -> executor.replaceWithClearTop(SettingsFragment.newInstance())
        }
    }

    override fun navigateToReportsFilter() {
        executor.replaceWithVerticalAnimation(ReportFilterFragment.newInstance(ReportFilterFragment.TARGET_LIST), true)
    }

    override fun navigateToNewReport() {
        Intent(activity, ReportRegistrationActivity::class.java).let {
            activity.startActivityForResult(it, ActivityConstants.REQUEST_CODE_NEW_REPORT)
        }
    }

    override fun navigateToProfileEditActivity() {
        Intent(activity, ProfileEditActivity::class.java).let {
            activity.startActivityForResult(it, ActivityConstants.REQUEST_EDIT_PROFILE)
        }
    }

    override fun navigateToProblemDetailActivity(problem: Problem) {
        ProblemDetailActivity.getStartActivityIntent(activity, problem.id).let {
            activity.startActivity(it)
        }
    }


    override fun showReportsImportDialog() {
        executor.showDialog(ReportImportDialogFragment.newInstance())
    }

    override fun onBackPressed(): Boolean {
        return executor.onBackPressed()
    }

}