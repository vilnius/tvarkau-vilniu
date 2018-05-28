package lt.vilnius.tvarkau.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.squareup.leakcanary.RefWatcher
import io.reactivex.Scheduler
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.dagger.IoScheduler
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.extensions.emptyToNull
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import lt.vilnius.tvarkau.interfaces.OnBackPressed
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.prefs.AppPreferences
import lt.vilnius.tvarkau.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */


abstract class BaseFragment : Fragment(), OnBackPressed {

    protected var screen: Screen? = null

    @Inject
    lateinit var legacyApiService: LegacyApiService
    @field:[Inject Named(Preferences.MY_PROBLEMS_PREFERENCES)]
    lateinit var myProblemsPreferences: SharedPreferences
    @Inject
    lateinit var appPreferences: AppPreferences
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
        get() = activity!! as BaseActivity?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screen = this::class.java.annotations.find { it is Screen } as Screen?
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onInject(ActivityComponent.init((activity!!.application as TvarkauApplication).component, activity!! as AppCompatActivity))
    }

    protected open fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun onResume() {
        super.onResume()

        screen?.run {
            if (titleRes != 0) {
                activity!!.setTitle(titleRes)
            }

            trackingScreenName.emptyToNull()?.let {
                analytics.trackOpenFragment(activity!!, it)
            }

            (activity!! as AppCompatActivity).let { act ->
                when (navigationMode) {
                    NavigationMode.DEFAULT ->
                        act.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    NavigationMode.CLOSE -> {
                        act.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        (activity!! as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
                    }
                    NavigationMode.BACK -> {
                        act.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        (activity!! as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                    }
                }
            }
        }
    }

    override fun onPause() {
        screen?.trackingScreenName?.emptyToNull()?.let(analytics::trackCloseFragment)

        super.onPause()
    }

    override fun onBackPressed(): Boolean = false

    override fun onDestroy() {
        super.onDestroy()
        refWatcher.watch(this)
    }
}