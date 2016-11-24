package lt.vilnius.tvarkau.fragments;


import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.events_listeners.MapInfoWindowShownEvent;
import lt.vilnius.tvarkau.views.adapters.MapsInfoWindowAdapter;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public abstract class BaseMapFragment extends SupportMapFragment
        implements GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    protected static final LatLng VILNIUS_LAT_LNG = new LatLng(54.687157, 25.279652);
    private static final float DEFAULT_ZOOM_LEVEL = 15f;
    private static final float CITY_BOUNDARY_DISTANCE = 15000f; //15km

    protected GoogleMap googleMap;

    protected BitmapDescriptor doneMarker;
    protected BitmapDescriptor postponedMarker;
    protected BitmapDescriptor registeredMarker;
    protected BitmapDescriptor transferredMarker;
    protected BitmapDescriptor selectedMarker;

    protected HashMap<String, Problem> problemHashMap = new HashMap<>();

    private GoogleApiClient googleApi;
    private Handler handler;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();

        setMarkerResources();

        if (googleApi == null) {
            googleApi = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .enableAutoManage(getActivity(), this)
                    .build();
        }
    }

    private void setMarkerResources() {
        doneMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_done);
        postponedMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_postponed);
        transferredMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_transferred);
        registeredMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_registered);
        selectedMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_selected);
    }

    protected void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMarkerClickListener(this);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VILNIUS_LAT_LNG, 10f));

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        initMapData();
    }

    private void zoomToMyLocation(GoogleMap map, Location lastLocation) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                DEFAULT_ZOOM_LEVEL
        ));
    }

    private void setMarkerInfoWindowAdapter() {
        googleMap.setInfoWindowAdapter(new MapsInfoWindowAdapter(getActivity(), problemHashMap));
    }

    protected abstract void initMapData();

    protected void populateMarkers() {
        for (Map.Entry<String, Problem> entry : problemHashMap.entrySet()) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(entry.getValue().getLatLng());
            markerOptions.title(entry.getKey());
            markerOptions.icon(getMarkerIcon(entry.getValue()));

            googleMap.addMarker(markerOptions);
        }

        setMarkerInfoWindowAdapter();
    }

    protected void placeAndShowMarker(Problem problem) {
        String problemStringId = String.valueOf(problem.getId());

        // Hack: Google Map don't have setData method.
        // There is no easy way to get problem from marker.
        // Set problem id as marker title and keep hashmap of problems with ids
        problemHashMap.put(problemStringId, problem);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(problem.getLatLng());
        markerOptions.title(problemStringId);
        markerOptions.icon(getMarkerIcon(problem));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(problem.getLatLng(), 12f));
        setMarkerInfoWindowAdapter();
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setIcon(selectedMarker);
        marker.showInfoWindow();
        //  There is a known issue where in InfoWindow the image
        // doesn't load properly the first time. Need to reload  each time
        final Handler handler = new Handler();
        handler.postDelayed(marker::showInfoWindow, 200);
    }

    public BitmapDescriptor getMarkerIcon(Problem problem) {
        switch (problem.getStatus()) {
            case Problem.STATUS_DONE:
                return doneMarker;
            case Problem.STATUS_RESOLVED:
                return doneMarker;
            case Problem.STATUS_POSTPONED:
                return postponedMarker;
            case Problem.STATUS_TRANSFERRED:
                return transferredMarker;
            case Problem.STATUS_REGISTERED:
                return registeredMarker;
            default:
                return registeredMarker;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getActivity().setTitle(getProblemByMarker(marker).getAddress());
        marker.setIcon(selectedMarker);
        EventBus.getDefault().post(new MapInfoWindowShownEvent(marker));
        marker.showInfoWindow();
        //  There is a known issue where in InfoWindow the image
        // doesn't load properly the first time. Need to reload  each time
        handler.postDelayed(marker::showInfoWindow, 200);
        return false;
    }

    public Problem getProblemByMarker(Marker marker) {
        return problemHashMap.get(marker.getTitle());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApi);
        boolean isInCityBoundaries = false;

        if (lastLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    VILNIUS_LAT_LNG.latitude, VILNIUS_LAT_LNG.longitude, results);

            isInCityBoundaries = results[0] <= CITY_BOUNDARY_DISTANCE;
        }

        if (isInCityBoundaries) {
            handler.post(() -> zoomToMyLocation(googleMap, lastLocation));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //don't care about that, can't do anything anyway
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //don't care about that, can't do anything anyway
    }

    @Override
    public void onDestroyView() {
        googleMap.setOnMarkerClickListener(null);
        googleMap = null;
        super.onDestroyView();
    }
}
