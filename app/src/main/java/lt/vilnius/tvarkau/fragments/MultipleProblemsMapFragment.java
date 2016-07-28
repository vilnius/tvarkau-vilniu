package lt.vilnius.tvarkau.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import lt.vilnius.tvarkau.API.ApiMethod;
import lt.vilnius.tvarkau.API.ApiRequest;
import lt.vilnius.tvarkau.API.ApiResponse;
import lt.vilnius.tvarkau.API.GetProblemsParams;
import lt.vilnius.tvarkau.API.LegacyApiModule;
import lt.vilnius.tvarkau.API.LegacyApiService;
import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Gediminas Zukas on 04/05/16.
 */

@AutoComponent(modules = LegacyApiModule.class)
@AutoInjector
@Singleton
public class MultipleProblemsMapFragment extends BaseMapFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    @Inject LegacyApiService legacyApiService;

    private static final int PROBLEM_COUNT_LIMIT_IN_MAP = 200;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DaggerMultipleProblemsMapFragmentComponent.create().inject(this);

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

        GetProblemsParams params = new GetProblemsParams(PROBLEM_COUNT_LIMIT_IN_MAP, null, null, null, null, null, null, null);
        ApiRequest<GetProblemsParams> request = new ApiRequest<>(ApiMethod.GET_PROBLEMS, params);

        Action1<ApiResponse<List<Problem>>> onSuccess = apiResponse -> {
            if (apiResponse.getResult().size() > 0) {
                for (Problem problem : apiResponse.getResult()){
                    placeMarkerOnTheMap(problem, false);
                }
                setMarkerInfoWindowAdapter();
            }
            else {
                Toast.makeText(getContext(), R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show();
            }
        };

        Action1<Throwable> onError = Throwable::printStackTrace;

        legacyApiService.getProblems(request)
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
}
