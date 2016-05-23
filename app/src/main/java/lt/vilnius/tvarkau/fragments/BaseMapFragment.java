package lt.vilnius.tvarkau.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.MapsInfoWindowAdapter;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by Karolis Vycius on 2016-01-13.
 */
public abstract class BaseMapFragment extends SupportMapFragment
        implements GoogleMap.OnMarkerClickListener {

    protected static final LatLng VILNIUS_LAT_LNG = new LatLng(54.687157, 25.279652);
    protected static final int GPS_PERMISSION_REQUEST_CODE = 11;
    protected static final String[] MAP_PERMISSIONS = new String[]{ACCESS_FINE_LOCATION};

    protected GoogleMap googleMap;

    protected BitmapDescriptor inProgressMarker;
    protected BitmapDescriptor doneMarker;
    protected BitmapDescriptor selectedMarker;

    protected HashMap<String, Problem> problemHashMap = new HashMap<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setMarkerResources();
    }

    private void setMarkerResources() {
        inProgressMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_blue);
        doneMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_green);
        selectedMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red);
    }

    protected void requestGPSPermission() {
        if (PermissionUtils.isAllPermissionsGranted(getActivity(), MAP_PERMISSIONS)) {
            googleMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(MAP_PERMISSIONS, GPS_PERMISSION_REQUEST_CODE);
        }
    }

    protected void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMarkerClickListener(this);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VILNIUS_LAT_LNG, 10f));

        requestGPSPermission();

        initMapData();
    }

    protected void setMarkerInfoWindowAdapter() {
        googleMap.setInfoWindowAdapter(new MapsInfoWindowAdapter(getActivity(), problemHashMap));
    }

    protected abstract void initMapData();

    protected void placeMarkerOnTheMap(Problem problem, boolean shouldShowInfoWindow) {
        String problemStringId = String.valueOf(problem.getId());

        // Hack: Google Map don't have setData method.
        // There is no easy way to get problem from marker.
        // Set problem id as marker title and keep hashmap of problems with ids
        problemHashMap.put(problemStringId, problem);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(problem.getLatLng());
        markerOptions.title(problemStringId);
        markerOptions.icon(getMarkerIcon(problem));

        if (shouldShowInfoWindow) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(problem.getLatLng(), 12f));
            setMarkerInfoWindowAdapter();
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setIcon(selectedMarker);
            marker.showInfoWindow();
        } else
            googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GPS_PERMISSION_REQUEST_CODE && PermissionUtils.isAllPermissionsGranted(getActivity(), MAP_PERMISSIONS)) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    public BitmapDescriptor getMarkerIcon(Problem problem) {
        switch (problem.getStatusCode()) {
            case Problem.STATUS_DONE:
                return doneMarker;
            default:
                return inProgressMarker;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getActivity().setTitle(getProblemByMarker(marker).getAddress());
        marker.setIcon(selectedMarker);

        return false;
    }

    public Problem getProblemByMarker(Marker marker) {
        return problemHashMap.get(marker.getTitle());
    }
}
