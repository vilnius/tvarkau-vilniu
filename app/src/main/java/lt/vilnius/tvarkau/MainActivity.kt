package lt.vilnius.tvarkau

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.IdRes
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.activity.available
import lt.vilnius.tvarkau.activity.googlePlayServicesAvailability
import lt.vilnius.tvarkau.activity.resolutionDialog
import lt.vilnius.tvarkau.activity.resultCode
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.navigation.NavigationManager.TabItem.MY_REPORTS_LIST
import lt.vilnius.tvarkau.navigation.NavigationManager.TabItem.REPORTS_LIST
import lt.vilnius.tvarkau.navigation.NavigationManager.TabItem.REPORTS_MAP
import lt.vilnius.tvarkau.navigation.NavigationManager.TabItem.SETTINGS
import lt.vilnius.tvarkau.prefs.AppPreferences
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var api: TvarkauMiestaApi
    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var navigationManager: NavigationManager

    private var googlePlayServicesResolutionDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        initialize(savedInstanceState)
        bottom_navigation.setOnNavigationItemSelectedListener {
            onTabSelected(it.itemId)
            true
        }
    }

    private fun onTabSelected(@IdRes id: Int) {
        when (id) {
            R.id.tab_list_of_problems -> navigationManager.navigateToMenuItem(REPORTS_LIST)
            R.id.tab_map_of_problems -> navigationManager.navigateToMenuItem(REPORTS_MAP)
            R.id.tab_my_problems -> navigationManager.navigateToMenuItem(MY_REPORTS_LIST)
            R.id.tab_settings -> navigationManager.navigateToMenuItem(SETTINGS)
            else -> throw IllegalArgumentException("Tab doesn't exists")
        }
    }

    private fun initialize(savedInstanceState: Bundle?) {
        if (appPreferences.selectedCity.isSet()) {
            initNavigation(savedInstanceState)
        } else {
            api.getCities()
                .map { it.cities }
                .subscribeBy(
                    onSuccess = {
                        appPreferences.selectedCity.set(it.first(), commit = true)
                        initNavigation(savedInstanceState)
                    },
                    onError = {
                        Timber.e(it)

                        //TODO do a better error handling. No network, network errors might lead to empty screen here
                    }
                )
        }
    }

    private fun initNavigation(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            onTabSelected(R.id.tab_list_of_problems)
        }
    }

    override fun onStart() {
        super.onStart()

        val googlePlayServicesAvailability = googlePlayServicesAvailability()
        if (!googlePlayServicesAvailability.available()) {
            analytics.trackGooglePlayServicesError(googlePlayServicesAvailability.resultCode())

            googlePlayServicesResolutionDialog?.dismiss()
            googlePlayServicesResolutionDialog = googlePlayServicesAvailability.resolutionDialog(this)
            googlePlayServicesResolutionDialog?.show()
        }
    }

    override fun onBackPressed() {
        if (navigationManager.onBackPressed()) {
            return
        }

        super.onBackPressed()
    }
}
