package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.os.Parcelable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import lt.vilnius.tvarkau.entity.Problem
import org.parceler.Parcels

class SingleProblemMapFragment : BaseMapFragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowCloseListener {

    internal var problem: Problem? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getMapAsync(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            problem = Parcels.unwrap<Problem>(arguments.getParcelable<Parcelable>(null))
        }
    }

    override fun initMapData() {
        addSingleProblemMarker()
    }

    private fun addSingleProblemMarker() {
        problem?.let { p ->
            placeAndShowMarker(p)
            setMapTitle(p.getAddress())
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        activity.onBackPressed()
    }

    override fun onInfoWindowClose(marker: Marker) {
        val problem = getProblemByMarker(marker)

        activity.title = problem.getAddress()
        marker.setIcon(getMarkerIcon(problem))
    }

    override fun onMapReady(map: GoogleMap) {
        super.onMapReady(map)

        map.setOnInfoWindowClickListener(this)
        map.setOnInfoWindowCloseListener(this)
    }

    private fun setMapTitle(address: String) {
        activity.title = address
    }

    companion object {

        fun getInstance(problem: Problem): SingleProblemMapFragment {
            val singleProblemMapFragment = SingleProblemMapFragment()
            val arguments = Bundle()
            arguments.putParcelable(null, Parcels.wrap(problem))
            singleProblemMapFragment.arguments = arguments
            return singleProblemMapFragment
        }
    }
}

