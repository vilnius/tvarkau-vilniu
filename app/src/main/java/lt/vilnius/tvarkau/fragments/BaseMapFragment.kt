package lt.vilnius.tvarkau.fragments


import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.MapInfoWindowShownEvent
import lt.vilnius.tvarkau.views.adapters.MapsInfoWindowAdapter
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

abstract class BaseMapFragment : SupportMapFragment(),
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    protected var googleMap: GoogleMap? = null

    protected val doneMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_done) }
    protected val postponedMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_postponed) }
    protected val registeredMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_registered) }
    protected val transferredMarker: BitmapDescriptor by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_transferred) }
    protected val selectedMarker: BitmapDescriptor  by lazy { BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_selected) }

    private var googleApi: GoogleApiClient? = null
    private var infoWindowAdapter: MapsInfoWindowAdapter? = null

    private lateinit var handler: Handler

    @Inject
    lateinit var legacyApiService: LegacyApiService

    @Inject
    lateinit var analytics: Analytics

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handler = Handler()

        (activity.application as TvarkauApplication).component.inject(this)

        if (googleApi == null) {
            googleApi = GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .enableAutoManage(activity, this)
                    .build()
        }
    }

    override fun onResume() {
        super.onResume()

        analytics.trackCurrentFragment(activity, this)
    }

    protected open fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.setOnMarkerClickListener(this)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VILNIUS_LAT_LNG, 10f))

        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }

        initMapData()
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

    protected abstract fun initMapData()

    protected fun populateMarkers(problems: List<Problem>) {
        problems.forEach { problem ->
            val markerOptions = MarkerOptions()
            markerOptions.position(problem.latLng)
            markerOptions.icon(getMarkerIcon(problem))

            val marker = googleMap!!.addMarker(markerOptions)
            marker.tag = problem
        }

        setMarkerInfoWindowAdapter()
    }

    protected fun placeAndShowMarker(problem: Problem) {
        val markerOptions = MarkerOptions()
        markerOptions.position(problem.latLng)
        markerOptions.icon(getMarkerIcon(problem))

        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(problem.latLng, 12f))
        setMarkerInfoWindowAdapter()
        val marker = googleMap!!.addMarker(markerOptions)
        marker.tag = problem
        marker.setIcon(selectedMarker)
        infoWindowAdapter?.showInfoWindow(marker)
    }

    fun getMarkerIcon(problem: Problem): BitmapDescriptor {
        return when (problem.getStatus()) {
            Problem.STATUS_DONE, Problem.STATUS_RESOLVED -> doneMarker
            Problem.STATUS_POSTPONED -> postponedMarker
            Problem.STATUS_TRANSFERRED -> transferredMarker
            Problem.STATUS_REGISTERED -> registeredMarker
            else -> registeredMarker
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        activity.title = (marker.tag as Problem).getAddress()
        marker.setIcon(selectedMarker)
        EventBus.getDefault().post(MapInfoWindowShownEvent(marker))
        infoWindowAdapter?.showInfoWindow(marker)

        return false
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
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

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        //don't care about that, can't do anything anyway
    }

    override fun onDestroyView() {
        infoWindowAdapter?.clearMarkerImages()

        googleMap?.setOnMarkerClickListener(null)
        googleMap = null
        super.onDestroyView()
    }

    companion object {

        protected val VILNIUS_LAT_LNG = LatLng(54.687157, 25.279652)
        private val DEFAULT_ZOOM_LEVEL = 15f
        private val CITY_BOUNDARY_DISTANCE = 15000f //15km
    }
}
