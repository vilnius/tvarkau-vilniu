package lt.vilnius.tvarkau.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import com.squareup.leakcanary.RefWatcher
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.dagger.module.IoScheduler
import lt.vilnius.tvarkau.dagger.module.UiScheduler
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import lt.vilnius.tvarkau.prefs.Preferences
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */
abstract class BaseFragment : Fragment() {

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onInject((activity.application as TvarkauApplication).component)
    }

    protected open fun onInject(component: ApplicationComponent) {
        component.inject(this)
    }

    override fun onResume() {
        super.onResume()

        analytics.trackCurrentFragment(activity, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        refWatcher.watch(this)
    }

    open fun onBackPressed(): Boolean {
        return false
    }
}