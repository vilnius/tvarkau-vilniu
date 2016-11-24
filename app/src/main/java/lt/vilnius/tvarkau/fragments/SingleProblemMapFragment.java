package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import org.parceler.Parcels;

import lt.vilnius.tvarkau.entity.Problem;

public class SingleProblemMapFragment extends BaseMapFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    Problem problem;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMapAsync(this);
    }

    public static SingleProblemMapFragment getInstance(Problem problem) {
        SingleProblemMapFragment singleProblemMapFragment = new SingleProblemMapFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(null, Parcels.wrap(problem));
        singleProblemMapFragment.setArguments(arguments);
        return singleProblemMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            problem = Parcels.unwrap(getArguments().getParcelable(null));
        }
    }

    @Override
    protected void initMapData() {
        addSingleProblemMarker();
    }

    private void addSingleProblemMarker() {
        if (problem != null) {
            placeAndShowMarker(problem);
            setMapTitle(problem.getAddress());
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getActivity().onBackPressed();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        Problem problem = getProblemByMarker(marker);

        getActivity().setTitle(problem.getAddress());
        marker.setIcon(getMarkerIcon(problem));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);

        map.setOnInfoWindowClickListener(this);
        map.setOnInfoWindowCloseListener(this);
    }

    private void setMapTitle(String address) {
        getActivity().setTitle(address);
    }
}

