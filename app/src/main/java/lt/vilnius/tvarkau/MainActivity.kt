package lt.vilnius.tvarkau

import android.app.Dialog
import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.activity.available
import lt.vilnius.tvarkau.activity.googlePlayServicesAvailability
import lt.vilnius.tvarkau.activity.resolutionDialog
import lt.vilnius.tvarkau.activity.resultCode
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.auth.SessionToken
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.dagger.component.MainActivityComponent
import lt.vilnius.tvarkau.navigation.BottomNavigationController
import lt.vilnius.tvarkau.prefs.AppPreferences
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var bottomNavigationController: BottomNavigationController
    @Inject
    lateinit var api: TvarkauMiestaApi
    @Inject
    lateinit var sessionToken: SessionToken
    @Inject
    lateinit var appPreferences: AppPreferences

    private var googlePlayServicesResolutionDialog: Dialog? = null

    override fun onInject(component: ActivityComponent) {
        MainActivityComponent.init(component, this).inject(this)
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (appPreferences.apiToken.isSet()) {
            initialize(savedInstanceState)
        } else {
            sessionToken.refreshGuestToken()
                .subscribeBy(
                    onComplete = { initialize(savedInstanceState) },
                    onError = { Timber.e(it) }
                )
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
        bottomNavigationController.onCreate(savedInstanceState == null)
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

}
