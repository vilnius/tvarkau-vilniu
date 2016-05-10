package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.factory.DummyProblems;

/**
 * Created by Gediminas Zukas on 04/05/16.
 */
public class SingleProblemMapFragment extends BaseMapFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMapAsync(this);
    }

    public static SingleProblemMapFragment getInstance() {
        return new SingleProblemMapFragment();
    }

    @Override
    protected void initMapData() {
        addSingleProblemMarker();
    }

    private void addSingleProblemMarker() {
        Problem problem;
        List<Problem> problems = DummyProblems.getProblems();

        if (problems != null && !problems.isEmpty()) {
            problem = problems.get(0);
            placeMarkerOnTheMap(problem, true);
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

