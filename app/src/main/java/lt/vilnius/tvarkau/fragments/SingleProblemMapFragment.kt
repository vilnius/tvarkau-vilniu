package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.os.Parcelable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.entity.Problem
import org.parceler.Parcels

@Screen(navigationMode = NavigationMode.BACK,
        trackingScreenName = ActivityConstants.SCREEN_REPORT_MAP)
class SingleProblemMapFragment : BaseMapFragment(),
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    val problem: Problem
        get() = Parcels.unwrap<Problem>(arguments.getParcelable<Parcelable>(null))

    private fun initMapData() {
        placeAndShowMarker(problem)
        setMapTitle(problem.address)
    }

    override fun onInfoWindowClick(marker: Marker) {
        activity.onBackPressed()
    }

    override fun onInfoWindowClose(marker: Marker) {
        val problem = marker.tag as Problem

        activity.title = problem.address
        marker.setIcon(getMarkerIcon(problem))
    }

    override fun onMapLoaded() {
        super.onMapLoaded()

        googleMap?.setOnInfoWindowClickListener(this)
        googleMap?.setOnInfoWindowCloseListener(this)

        initMapData()
    }

    private fun setMapTitle(address: String?) {
        activity.title = address
    }

    companion object {

        fun getInstance(problem: Problem): SingleProblemMapFragment {
            return SingleProblemMapFragment().apply {
                arguments = Bundle()
                arguments.putParcelable(null, Parcels.wrap(problem))
            }
        }
    }
}

