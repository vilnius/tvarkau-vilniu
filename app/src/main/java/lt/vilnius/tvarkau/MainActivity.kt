package lt.vilnius.tvarkau

import android.os.Bundle
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.dagger.component.MainActivityComponent
import lt.vilnius.tvarkau.navigation.BottomNavigationController
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var bottomNavigationController: BottomNavigationController

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

}
