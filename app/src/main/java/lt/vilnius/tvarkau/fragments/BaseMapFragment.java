package lt.vilnius.tvarkau.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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

import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.events.MapInfoWindowShownEvent;
import lt.vilnius.tvarkau.views.adapters.MapsInfoWindowAdapter;

/**
 * Created by Karolis Vycius on 2016-01-13.
 */
public abstract class BaseMapFragment extends SupportMapFragment
        implements GoogleMap.OnMarkerClickListener {

    protected static final LatLng VILNIUS_LAT_LNG = new LatLng(54.687157, 25.279652);

    protected GoogleMap googleMap;

    protected BitmapDescriptor inProgressMarker;
    protected BitmapDescriptor doneMarker;
    protected BitmapDescriptor postponedMarker;
    protected BitmapDescriptor registeredMarker;
    protected BitmapDescriptor transferredMarker;
    protected BitmapDescriptor selectedMarker;

    protected HashMap<String, Problem> problemHashMap = new HashMap<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setMarkerResources();
    }

    private void setMarkerResources() {
        doneMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_done);
        postponedMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_postponed);
        transferredMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_transferred);
        registeredMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_registered);
        // In progress can be inactive
        inProgressMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_in_progress);
        selectedMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_selected);
    }

    protected void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMarkerClickListener(this);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VILNIUS_LAT_LNG, 10f));

        googleMap.setMyLocationEnabled(true);
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
            //  There is a known issue where in InfoWindow the image
            // doesn't load properly the first time. Need to reload  each time
            final Handler handler = new Handler();
            handler.postDelayed(marker::showInfoWindow, 200);
        } else
            googleMap.addMarker(markerOptions);
    }

    public BitmapDescriptor getMarkerIcon(Problem problem) {
        switch (problem.getStatus()) {
            case Problem.STATUS_DONE:
                return doneMarker;
            case Problem.STATUS_RESOLVED:
                return doneMarker;
            case Problem.STATUS_IN_PROGRESS:
                return inProgressMarker;
            case Problem.STATUS_POSTPONED:
                return postponedMarker;
            case Problem.STATUS_TRANSFERRED:
                return transferredMarker;
            case Problem.STATUS_REGISTERED:
                return registeredMarker;
            default:
                return inProgressMarker;
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
        final Handler handler = new Handler();
        handler.postDelayed(marker::showInfoWindow, 200);
        return false;
    }

    public Problem getProblemByMarker(Marker marker) {
        return problemHashMap.get(marker.getTitle());
    }
}
