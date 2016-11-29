package lt.vilnius.tvarkau.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.backend.LegacyApiService
import javax.inject.Inject

/**
 * @author Martynas Jurkus
 */
abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var legacyApiService: LegacyApiService
    @Inject
    lateinit var myProblemsPreferences: SharedPreferences
    @Inject
    lateinit var analytics: Analytics

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity.application as TvarkauApplication).component.inject(this)
    }

    override fun onResume() {
        super.onResume()

        analytics.trackCurrentFragment(activity, this)
    }
}