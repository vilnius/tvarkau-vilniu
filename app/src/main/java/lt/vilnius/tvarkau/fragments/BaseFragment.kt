package lt.vilnius.tvarkau.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.squareup.leakcanary.RefWatcher
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.R
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
abstract class BaseFragment @JvmOverloads constructor(
        init: Builder.() -> Unit = {}
) : Fragment(), OnBackPressed {

    var builder = Builder(init)

    enum class NavigationMode {
        DEFAULT,
        CLOSE,
        BACK,
    }

    class Builder private constructor() {

        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        var titleRes: Int? = null
        var navigationMode: NavigationMode = BaseFragment.NavigationMode.DEFAULT
        var trackingScreenName: String? = null

        fun titleRes(init: Builder.() -> Int) = apply { titleRes = init() }

        fun navigationMode(init: Builder.() -> NavigationMode) = apply { navigationMode = init() }

        fun trackingScreenName(init: Builder.() -> String) = apply { trackingScreenName = init() }
    }

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

        builder.trackingScreenName?.let { analytics.trackOpenFragment(activity, it) }

        builder.titleRes?.let(activity::setTitle)

        (activity as AppCompatActivity).let { act ->
            when (builder.navigationMode) {
                BaseFragment.NavigationMode.DEFAULT ->
                    act.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                BaseFragment.NavigationMode.CLOSE -> {
                    act.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
                }
                BaseFragment.NavigationMode.BACK -> {
                    act.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                }
            }
        }
    }

    override fun onPause() {
        builder.trackingScreenName?.let { analytics.trackCloseFragment(it) }

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        refWatcher.watch(this)
    }

    override fun onBackPressed(): Boolean = navigationManager.onBackPressed()
}