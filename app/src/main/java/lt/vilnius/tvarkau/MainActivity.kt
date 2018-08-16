package lt.vilnius.tvarkau

import android.app.Dialog
import android.os.Bundle
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.activity.available
import lt.vilnius.tvarkau.activity.googlePlayServicesAvailability
import lt.vilnius.tvarkau.activity.resolutionDialog
import lt.vilnius.tvarkau.activity.resultCode
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.dagger.component.MainActivityComponent
import lt.vilnius.tvarkau.navigation.BottomNavigationController
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var bottomNavigationController: BottomNavigationController

    private var googlePlayServicesResolutionDialog: Dialog? = null

    override fun onInject(component: ActivityComponent) {
        MainActivityComponent.init(component, this).inject(this)
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

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
