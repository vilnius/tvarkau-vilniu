package lt.vilnius.tvarkau

import android.os.Bundle
import android.os.Parcelable
import com.google.android.gms.maps.model.Marker
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.MapInfoWindowShownEvent
import lt.vilnius.tvarkau.fragments.*
import lt.vilnius.tvarkau.utils.GlobalConsts
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.parceler.Parcels

class ProblemsMapActivity : BaseActivity(),
        ReportFilterFragment.FilterSubmitListener {

    private var infoWindowMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.problems_map_activity)

        val data = intent.extras
        if (data != null && savedInstanceState == null) {
            val fragment: BaseMapFragment
            val fragmentTag = data.getString(GlobalConsts.KEY_MAP_FRAGMENT)

            when (fragmentTag) {
                GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT -> {
                    val problem = Parcels.unwrap<Problem>(data.getParcelable<Parcelable>(ProblemDetailFragment.KEY_PROBLEM))
                    fragment = SingleProblemMapFragment.getInstance(problem)
                }
                GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT -> fragment = MultipleProblemsMapFragment.newInstance()
                else -> return
            }

            supportFragmentManager.beginTransaction()
                    .replace(R.id.problems_map_frame, fragment)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (infoWindowMarker?.isInfoWindowShown ?: false) {
            infoWindowMarker!!.hideInfoWindow()
        } else {
            super.onBackPressed()
        }
    }

    fun openFilters() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_top, 0, 0, R.anim.slide_out_to_top)
                .replace(R.id.problems_map_frame, ReportFilterFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    override fun filterSubmitted() {
        onBackPressed()
    }

    @Subscribe
    fun onEvent(event: MapInfoWindowShownEvent) {
        infoWindowMarker = event.marker
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}
