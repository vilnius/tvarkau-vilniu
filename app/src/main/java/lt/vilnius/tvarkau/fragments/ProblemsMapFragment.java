package lt.vilnius.tvarkau.fragments;

import android.Manifest;
import android.content.Intent;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.factory.DummyProblems;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.MapsInfoWindowAdapter;

/**
 * Created by Karolis Vycius on 2016-01-13.
 */
public class ProblemsMapFragment extends SupportMapFragment
        implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GoogleMap.OnMarkerClickListener {

    protected static final LatLng VILNIUS_LAT_LNG = new LatLng(54.687157, 25.279652);
    protected static final int GPS_PERMISSION_REQUEST_CODE = 11;

    protected GoogleMap googleMap;

    protected BitmapDescriptor inProgressMarker;
    protected BitmapDescriptor doneMarker;

    protected HashMap<String, Problem> problemHashMap = new HashMap<>();

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
        inProgressMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_blue);
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
        map.setOnInfoWindowClickListener(this);
        map.setOnInfoWindowCloseListener(this);
        map.setOnMarkerClickListener(this);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VILNIUS_LAT_LNG, 10f));

        requestGPSPermission();


        for (Problem problem : DummyProblems.getProblems()) {
            String problemStringId = String.valueOf(problem.getId());

            // Hack: Google Map don't have setData method.
            // There is no easy way to get problem from marker.
            // Set problem id as marker title and keep hashmap of problems with ids
            problemHashMap.put(problemStringId, problem);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(problem.getLatLng());
            markerOptions.title(problemStringId);

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

        map.setInfoWindowAdapter(new MapsInfoWindowAdapter(getActivity(), problemHashMap));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == GPS_PERMISSION_REQUEST_CODE) {
            if (permissions.length >= 1 &&
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        int problemId = getProblemByMarker(marker).getId();

        Intent intent = ProblemDetailActivity.getStartActivityIntent(getActivity(), problemId);

        startActivity(intent);
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        getActivity().setTitle(R.string.title_problems_map);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getActivity().setTitle(getProblemByMarker(marker).getAddress());
        return false;
    }

    public Problem getProblemByMarker(Marker marker) {
        return problemHashMap.get(marker.getTitle());
    }
}
