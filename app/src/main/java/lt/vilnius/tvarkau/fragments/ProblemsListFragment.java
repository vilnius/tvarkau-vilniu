package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lt.vilnius.tvarkau.API.ApiMethod;
import lt.vilnius.tvarkau.API.ApiRequest;
import lt.vilnius.tvarkau.API.ApiResponse;
import lt.vilnius.tvarkau.API.GetProblemsParams;
import lt.vilnius.tvarkau.API.LegacyApiModule;
import lt.vilnius.tvarkau.API.LegacyApiService;
import lt.vilnius.tvarkau.events_listeners.EndlessRecyclerViewScrollListener;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.views.adapters.ProblemsListAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Karolis Vycius on 2016-01-13.
 */

@AutoComponent(modules = LegacyApiModule.class)
@AutoInjector
@Singleton
public class ProblemsListFragment extends Fragment {

    @Inject LegacyApiService legacyApiService;

    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.problem_list) RecyclerView recyclerView;

    public static final int PROBLEM_COUNT_LIMIT_PER_PAGE = 100;
    public List<Problem> problemList;
    public ProblemsListAdapter adapter;
    private Unbinder unbinder;
    private CompositeSubscription subscriptions;

    public static ProblemsListFragment getInstance() {
        return new ProblemsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        problemList = new ArrayList<>();
        subscriptions = new CompositeSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.problem_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        DaggerProblemsListFragmentComponent.create().inject(this);

        swipeContainer.setOnRefreshListener(() -> getData(0));
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override public void onLoadMore(int page, int totalItemsCount) {
                getData(page);
            }
        });

        adapter = new ProblemsListAdapter(getActivity(), problemList);
        recyclerView.setAdapter(adapter);

        getData(0);

        return view;
    }

    private void setupView() {
        adapter.notifyDataSetChanged();
    }

    private void getData(int page) {

        int startLoadingFromPage = page * PROBLEM_COUNT_LIMIT_PER_PAGE;

        GetProblemsParams params = new GetProblemsParams(startLoadingFromPage, PROBLEM_COUNT_LIMIT_PER_PAGE,
            null, null, null, null, null, null);
        ApiRequest<GetProblemsParams> request = new ApiRequest<>(ApiMethod.GET_PROBLEMS, params);

        Action1<ApiResponse<List<Problem>>> onSuccess = apiResponse -> {
            if (apiResponse.getResult().size() > 0) {
                problemList.addAll(apiResponse.getResult());
                setupView();
                swipeContainer.setRefreshing(false);
            } else {
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (subscriptions != null) {
            subscriptions.unsubscribe();
            subscriptions = new CompositeSubscription();
        }
    }
}
