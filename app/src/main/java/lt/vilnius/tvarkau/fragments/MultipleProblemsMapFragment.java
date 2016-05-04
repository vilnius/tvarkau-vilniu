package lt.vilnius.tvarkau.fragments;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.factory.DummyProblems;

/**
 * Created by Gediminas Zukas on 04/05/16.
 */
public class MultipleProblemsMapFragment extends BaseMapFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMapAsync(this);
    }

    public static MultipleProblemsMapFragment getInstance() {
        return new MultipleProblemsMapFragment();
    }

    @Override
    protected void initMapData() {
        addMultipleProblemsMarkers();
    }

    private void addMultipleProblemsMarkers() {
        for (Problem problem : DummyProblems.getProblems()) {
            placeMarkerOnTheMap(problem, false);
        }

        setMarkerInfoWindowAdapter();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int problemId = getProblemByMarker(marker).getId();

        Intent intent = ProblemDetailActivity.getStartActivityIntent(getActivity(), problemId);

        startActivity(intent);
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        Problem problem = getProblemByMarker(marker);

        getActivity().setTitle(R.string.title_problems_map);
        marker.setIcon(getMarkerIcon(problem));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);

        map.setOnInfoWindowClickListener(this);
        map.setOnInfoWindowCloseListener(this);
    }
}
