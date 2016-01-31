package lt.vilnius.tvarkau.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.factory.DummyProblems;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.MapsInfoWindowAdapter;

/**
 * Created by Karolis Vycius on 2016-01-13.
 */
public class ProblemsMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    protected static final LatLng VILNIUS_LAT_LNG = new LatLng(54.687157, 25.279652);
    protected static final int GPS_PERMISSION_REQUEST_CODE = 11;

    protected GoogleMap googleMap;

    protected static BitmapDescriptor inProgressMarker;
    protected static BitmapDescriptor doneMarker;

    public static ProblemsMapFragment getInstance() {
        return new ProblemsMapFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setMarkerResources();


        getMapAsync(this);
    }

    private void setMarkerResources() {
        inProgressMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red);
        doneMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_green);
    }

    protected void requestGPSPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            PermissionUtils.verifyAndRequestPermissions(getActivity(), GPS_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VILNIUS_LAT_LNG, 10f));

        requestGPSPermission();

        map.setInfoWindowAdapter(new MapsInfoWindowAdapter(getActivity().getLayoutInflater()));

        for (Problem problem : DummyProblems.getProblems()) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(problem.getLatLng());

            switch (problem.getStatusCode()) {
                case Problem.STATUS_IN_PROGRESS:
                    markerOptions.icon(inProgressMarker);
                    break;
                case Problem.STATUS_DONE:
                    markerOptions.icon(doneMarker);
                    break;
            }

            map.addMarker(markerOptions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == GPS_PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Try/catch is used only to suppress permission grant error. It's impossible scenario.
                try {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } catch (SecurityException securityException) {

                }
            }
        }
    }
}
