package lt.vilnius.tvarkau

import android.os.Bundle
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.dagger.component.MainActivityComponent
import lt.vilnius.tvarkau.navigation.BottomNavigationController
import javax.inject.Inject


/**
 * An activity representing a main activity home screen
 */
class MainActivity : BaseActivity() {

    @Inject
    lateinit var bottomNavigationController: BottomNavigationController

    override fun onInject(applicationComponent: ApplicationComponent) {
        MainActivityComponent.init(applicationComponent, this).inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        bottomNavigationController.onCreate()
    }

    override fun onStart() {
        super.onStart()

        bottomNavigationController.onStart()
    }

    companion object {
        const val MAP_PERMISSION_REQUEST_CODE = 11
        const val NEW_ISSUE_REQUEST_CODE = 12
        val MAP_PERMISSIONS = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
