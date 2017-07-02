package lt.vilnius.tvarkau.navigation

import android.app.Activity
import android.support.annotation.IdRes
import kotlinx.android.synthetic.main.activity_main.*
import lt.vilnius.tvarkau.R


class BottomNavigationController(private val activity: Activity, private val navigationManager: NavigationManager)
    : NavigationController {

    override fun onCreate() {
        activity.bottom_navigation.setOnNavigationItemSelectedListener {
            item ->
            onTabSelected(item.itemId)
            true
        }
    }

    override fun onStart() {
        activity.bottom_navigation.selectedItemId = R.id.tab_list_of_problems
    }

    private fun onTabSelected(@IdRes id: Int) {
        when (id) {
            R.id.tab_list_of_problems -> navigationManager.navigateToMenuItem(NavigationManager.TabItem.REPORTS_LIST)
            R.id.tab_map_of_problems -> navigationManager.navigateToMenuItem(NavigationManager.TabItem.REPORTS_MAP)
            R.id.tab_my_problems -> navigationManager.navigateToMenuItem(NavigationManager.TabItem.MY_REPORTS_LIST)
            R.id.tab_settings -> navigationManager.navigateToMenuItem(NavigationManager.TabItem.SETTINGS)
            else -> throw IllegalArgumentException("Tab doesn't exists")
        }
    }

}