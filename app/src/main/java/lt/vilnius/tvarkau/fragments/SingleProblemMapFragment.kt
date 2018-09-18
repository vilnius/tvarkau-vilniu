package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.entity.ReportEntity

@Screen(
    navigationMode = NavigationMode.BACK,
    trackingScreenName = ActivityConstants.SCREEN_REPORT_MAP
)
class SingleProblemMapFragment : BaseMapFragment(),
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnInfoWindowCloseListener {

    val problem: Problem
        get() = arguments!!.getParcelable(ARG_REPORT)

    private fun initMapData() {
        placeAndShowMarker(problem)
        setMapTitle(problem.address)
    }

    override fun onInfoWindowClick(marker: Marker) {
        activity!!.onBackPressed()
    }

    override fun onInfoWindowClose(marker: Marker) {
        val problem = marker.tag as Problem

        activity!!.title = problem.address
        marker.setIcon(getMarkerIcon(problem))
    }

    override fun onMapLoaded() {
        super.onMapLoaded()

        googleMap?.setOnInfoWindowClickListener(this)
        googleMap?.setOnInfoWindowCloseListener(this)

        initMapData()
    }

    private fun setMapTitle(address: String?) {
        activity!!.title = address
    }

    companion object {
        private const val ARG_REPORT = "report"

        fun getInstance(reportEntity: ReportEntity): SingleProblemMapFragment {
            return SingleProblemMapFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_REPORT, reportEntity)
                }
            }
        }
    }
}

