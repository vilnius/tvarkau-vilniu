package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.os.Parcelable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import lt.vilnius.tvarkau.entity.Problem
import org.parceler.Parcels

class SingleProblemMapFragment : BaseMapFragment(),
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    val problem: Problem
        get() = Parcels.unwrap<Problem>(arguments.getParcelable<Parcelable>(null))


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getMapAsync(this)
    }


    override fun initMapData() {
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

    override fun onMapReady(map: GoogleMap) {
        super.onMapReady(map)

        map.setOnInfoWindowClickListener(this)
        map.setOnInfoWindowCloseListener(this)
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

