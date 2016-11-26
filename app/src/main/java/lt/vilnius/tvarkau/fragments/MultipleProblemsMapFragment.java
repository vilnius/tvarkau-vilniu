package lt.vilnius.tvarkau.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import javax.inject.Singleton;

import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.backend.ApiMethod;
import lt.vilnius.tvarkau.backend.ApiRequest;
import lt.vilnius.tvarkau.backend.ApiResponse;
import lt.vilnius.tvarkau.backend.GetProblemsParams;
import lt.vilnius.tvarkau.entity.Problem;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class MultipleProblemsMapFragment extends BaseMapFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    private static final int PROBLEM_COUNT_LIMIT_IN_MAP = 200;

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

        GetProblemsParams params = new GetProblemsParams.Builder()
                .setStart(0)
                .setLimit(PROBLEM_COUNT_LIMIT_IN_MAP)
                .setDescriptionFilter(null)
                .setTypeFilter(null)
                .setAddressFilter(null)
                .setReporterFilter(null)
                .setDateFilter(null)
                .setStatusFilter(null)
                .create();
        ApiRequest<GetProblemsParams> request = new ApiRequest<>(ApiMethod.GET_PROBLEMS, params);

        Action1<List<Problem>> onSuccess = this::populateMarkers;

        Action1<Throwable> onError = (throwable) -> {
            Toast.makeText(getContext(), R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show();
            Timber.e(throwable);
        };

        legacyApiService.getProblems(request)
                .toSingle()
                .map(ApiResponse::getResult)
                .doOnSuccess(problems -> {
                    if (problems.isEmpty()) {
                        throw new IllegalStateException("Empty problem list returned");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        onError
                );
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String problemId = getProblemByMarker(marker).getId();

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

    @Override
    public void onLocationInsideCity(Location location) {
        zoomToMyLocation(googleMap, location);
    }
}
