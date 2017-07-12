package lt.vilnius.tvarkau.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.squareup.leakcanary.RefWatcher
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.dagger.module.IoScheduler
import lt.vilnius.tvarkau.dagger.module.UiScheduler
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import lt.vilnius.tvarkau.interfaces.OnBackPressed
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.prefs.Preferences
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */
abstract class BaseFragment : Fragment(), OnBackPressed {

    @Inject
    lateinit var legacyApiService: LegacyApiService
    @field:[Inject Named(Preferences.MY_PROBLEMS_PREFERENCES)]
    lateinit var myProblemsPreferences: SharedPreferences
    @Inject
    lateinit var analytics: Analytics
    @Inject
    lateinit var refWatcher: RefWatcher
    @field:[Inject IoScheduler]
    lateinit var ioScheduler: Scheduler
    @field:[Inject UiScheduler]
    lateinit var uiScheduler: Scheduler
    @Inject
    lateinit var connectivityProvider: ConnectivityProvider
    @Inject
    lateinit var navigationManager: NavigationManager

    protected val baseActivity: BaseActivity?
        get() = activity as BaseActivity?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onInject(ActivityComponent.init((activity.application as TvarkauApplication).component, activity as AppCompatActivity))
    }

    protected open fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun onResume() {
        super.onResume()

        analytics.trackOpenFragment(activity, javaClass.simpleName)
    }

    override fun onPause() {
        analytics.trackCloseFragment(javaClass.simpleName)

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        refWatcher.watch(this)
    }

    override fun onBackPressed(): Boolean = navigationManager.onBackPressed()
}