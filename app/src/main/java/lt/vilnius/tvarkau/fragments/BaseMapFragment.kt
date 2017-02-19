package lt.vilnius.tvarkau.fragments

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_map_fragment.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.MapInfoWindowShownEvent
import lt.vilnius.tvarkau.views.adapters.MapsInfoWindowAdapter
import org.greenrobot.eventbus.EventBus

abstract class BaseMapFragment : BaseFragment(),
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks {

    protected var googleMap: GoogleMap? = null

    protected val doneMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_done) }
    protected val postponedMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_postponed) }
    protected val registeredMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_registered) }
    protected val transferredMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_transferred) }
    protected val selectedMarker: BitmapDescriptor  by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_selected) }

    private var googleApi: GoogleApiClient? = null
    private var infoWindowAdapter: MapsInfoWindowAdapter? = null

    private lateinit var handler: Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //workaround for support library issue
        //https://code.google.com/p/android/issues/detail?id=196430
        val mapState = savedInstanceState?.getBundle(KEY_MAP_SAVED_STATE)
        map_container.onCreate(mapState)
        map_container.onResume()

        with(activity as BaseActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                android.R.id.home -> {
                    activity.onBackPressed()
                    true
                }
                else -> onOptionsItemSelected(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handler = Handler()

        if (googleApi == null) {
            googleApi = GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build()
        }

        map_container.getMapAsync {
            googleMap = it
            onMapLoaded()
        }
    }

    override fun onStart() {
        super.onStart()
        googleApi?.connect()
    }

    override fun onPause() {
        super.onPause()
        googleApi?.disconnect()
    }

    open protected fun onMapLoaded() {
        googleMap?.setOnMarkerClickListener(this)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(VILNIUS_LAT_LNG, 10f))

        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    protected fun zoomToMyLocation(map: GoogleMap, lastLocation: Location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(lastLocation.latitude, lastLocation.longitude),
                DEFAULT_ZOOM_LEVEL
        ))
    }

    private fun setMarkerInfoWindowAdapter() {
        infoWindowAdapter = MapsInfoWindowAdapter(activity)
        googleMap?.setInfoWindowAdapter(infoWindowAdapter)
    }

    protected fun populateMarkers(problems: List<Problem>) {
        googleMap?.clear()

        problems.forEach { problem ->
            val markerOptions = MarkerOptions()
            markerOptions.position(problem.latLng)
            markerOptions.icon(getMarkerIcon(problem))

            googleMap?.addMarker(markerOptions)?.apply {
                tag = problem
            }
        }

        setMarkerInfoWindowAdapter()
    }

    protected fun placeAndShowMarker(problem: Problem) {
        val markerOptions = MarkerOptions()
        markerOptions.position(problem.latLng)
        markerOptions.icon(getMarkerIcon(problem))

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(problem.latLng, 12f))
        setMarkerInfoWindowAdapter()
        googleMap?.addMarker(markerOptions)?.apply {
            tag = problem
            setIcon(selectedMarker)
            infoWindowAdapter?.showInfoWindow(this)
        }
    }

    fun getMarkerIcon(problem: Problem): BitmapDescriptor {
        return when (problem.status) {
            Problem.STATUS_DONE, Problem.STATUS_RESOLVED -> doneMarker
            Problem.STATUS_POSTPONED -> postponedMarker
            Problem.STATUS_TRANSFERRED -> transferredMarker
            Problem.STATUS_REGISTERED -> registeredMarker
            else -> registeredMarker
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        activity.title = (marker.tag as Problem).address
        marker.setIcon(selectedMarker)
        EventBus.getDefault().post(MapInfoWindowShownEvent(marker))
        infoWindowAdapter?.showInfoWindow(marker)

        return false
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return
        }

        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApi)
        var isInCityBoundaries = false

        lastLocation?.let {
            val results = FloatArray(1)
            Location.distanceBetween(lastLocation.latitude, lastLocation.longitude,
                    VILNIUS_LAT_LNG.latitude, VILNIUS_LAT_LNG.longitude, results)

            isInCityBoundaries = results[0] <= CITY_BOUNDARY_DISTANCE
        }

        if (isInCityBoundaries) {
            handler.post { onLocationInsideCity(lastLocation) }
        }
    }

    open fun onLocationInsideCity(location: Location) {
    }

    override fun onConnectionSuspended(i: Int) {
        //don't care about that, can't do anything anyway
    }

    override fun onDestroy() {
        super.onDestroy()
        map_container?.onDestroy()
    }

    override fun onDestroyView() {
        infoWindowAdapter?.clearMarkerImages()

        googleMap?.setOnMarkerClickListener(null)
        googleMap = null
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapState = Bundle()
        map_container.onSaveInstanceState(mapState)
        outState.putBundle(KEY_MAP_SAVED_STATE, mapState)
    }

    companion object {
        protected val VILNIUS_LAT_LNG = LatLng(54.687157, 25.279652)
        private const val DEFAULT_ZOOM_LEVEL = 15f
        private const val CITY_BOUNDARY_DISTANCE = 15000f //15km
        private const val KEY_MAP_SAVED_STATE = "map_save_state"
    }
}
