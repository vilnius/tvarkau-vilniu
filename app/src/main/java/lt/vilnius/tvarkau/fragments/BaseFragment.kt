package lt.vilnius.tvarkau.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.dagger.module.IoScheduler
import lt.vilnius.tvarkau.dagger.module.UiScheduler
import rx.Scheduler
import javax.inject.Inject

/**
 * @author Martynas Jurkus
 */
abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var legacyApiService: LegacyApiService
    @Inject
    lateinit var myProblemsPreferences: SharedPreferences
    @field:[Inject IoScheduler]
    lateinit var ioScheduler: Scheduler
    @field:[Inject UiScheduler]
    lateinit var uiScheduler: Scheduler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity.application as TvarkauApplication).component.inject(this)
    }
}