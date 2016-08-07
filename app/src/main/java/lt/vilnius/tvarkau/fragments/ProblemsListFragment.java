package lt.vilnius.tvarkau.fragments;

import android.content.SharedPreferences;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lt.vilnius.tvarkau.AppModule;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.SharedPreferencesModule;
import lt.vilnius.tvarkau.api.ApiMethod;
import lt.vilnius.tvarkau.api.ApiRequest;
import lt.vilnius.tvarkau.api.ApiResponse;
import lt.vilnius.tvarkau.api.GetProblemParams;
import lt.vilnius.tvarkau.api.GetProblemsParams;
import lt.vilnius.tvarkau.api.LegacyApiModule;
import lt.vilnius.tvarkau.api.LegacyApiService;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.events_listeners.EndlessRecyclerViewScrollListener;
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent;
import lt.vilnius.tvarkau.views.adapters.ProblemsListAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Karolis Vycius on 2016-01-13.
 */

@AutoComponent(modules = {LegacyApiModule.class, AppModule.class, SharedPreferencesModule.class})
@AutoInjector
@Singleton
public class ProblemsListFragment extends Fragment {

    @Inject LegacyApiService legacyApiService;
    @Inject SharedPreferences myProblemsPreferences;

    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.problem_list) RecyclerView recyclerView;
    @BindView(R.id.my_problems_empty_view) View myProblemsEmptyView;

    private static final int PROBLEM_COUNT_LIMIT_PER_PAGE = 20;
    private static final String ALL_PROBLEM_LIST = "all_problem_list";
    private List<Problem> problemList;
    private ProblemsListAdapter adapter;
    private Unbinder unbinder;
    private CompositeSubscription subscriptions;
    private boolean isAllProblemList;
    private boolean shouldLoadMoreProblems;
    private boolean isLoading;

    public static ProblemsListFragment getAllProblemList() {
        ProblemsListFragment problemsListFragment = new ProblemsListFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(ALL_PROBLEM_LIST, true);
        problemsListFragment.setArguments(arguments);
        return problemsListFragment;
    }

    public static ProblemsListFragment getMyProblemList() {
        ProblemsListFragment problemsListFragment = new ProblemsListFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(ALL_PROBLEM_LIST, false);
        problemsListFragment.setArguments(arguments);
        return problemsListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerProblemsListFragmentComponent
            .builder()
            .appModule(new AppModule(this.getActivity().getApplication()))
            .sharedPreferencesModule(new SharedPreferencesModule())
            .legacyApiModule(new LegacyApiModule())
            .build()
            .inject(this);

        if (getArguments() != null) {
            isAllProblemList = getArguments().getBoolean(ALL_PROBLEM_LIST);
        }

        problemList = new ArrayList<>();
        subscriptions = new CompositeSubscription();
        shouldLoadMoreProblems = true;
        isLoading = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.problem_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        swipeContainer.setOnRefreshListener(this::reloadData);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override public void onLoadMore(int page, int totalItemsCount) {
                if (!isLoading) {
                    getData(page);
                }
            }
        });

        adapter = new ProblemsListAdapter(getActivity(), problemList);
        recyclerView.setAdapter(adapter);

        myProblemsEmptyView.setVisibility(View.GONE);

        if (problemList.size() == 0 && shouldLoadMoreProblems) {
            getData(0);
        }

        return view;
    }

    private void reloadData() {
        problemList.clear();
        getData(0);
    }

    private void setupView() {
        adapter.notifyDataSetChanged();

        if (!isAllProblemList && problemList.size() == 0) {
            myProblemsEmptyView.setVisibility(View.VISIBLE);
            swipeContainer.setRefreshing(false);
        }
    }

    private void getData(int page) {
        if (isLoading) {
            swipeContainer.setRefreshing(false);
            return;
        }

        isLoading = true;
        if (isAllProblemList) {
            loadAllProblems(page);
        } else {
            loadMyProblems();
        }
    }

    private void loadAllProblems(int page) {
        if (shouldLoadMoreProblems) {

            int startLoadingFromPage = page * PROBLEM_COUNT_LIMIT_PER_PAGE;

            GetProblemsParams params = new GetProblemsParams.Builder()
                .setStart(startLoadingFromPage)
                .setLimit(PROBLEM_COUNT_LIMIT_PER_PAGE)
                .setDescriptionFilter(null)
                .setTypeFilter(null)
                .setAddressFilter(null)
                .setReporterFilter(null)
                .setDateFilter(null)
                .setStatusFilter(null)
                .create();
            ApiRequest<GetProblemsParams> request = new ApiRequest<>(ApiMethod.GET_PROBLEMS, params);

            Action1<ApiResponse<List<Problem>>> onSuccess = apiResponse -> {
                shouldLoadMoreProblems = apiResponse.getResult().size() == PROBLEM_COUNT_LIMIT_PER_PAGE;
                isLoading = false;

                if (!shouldLoadMoreProblems) {
                    adapter.hideLoader();
                }

                if (apiResponse.getResult().size() > 0) {
                    problemList.addAll(apiResponse.getResult());
                    setupView();
                    swipeContainer.setRefreshing(false);
                }
            };

            Action1<Throwable> onError = throwable -> {
                throwable.printStackTrace();
                Toast.makeText(getContext(), R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
            };

            legacyApiService.getProblems(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    onSuccess,
                    onError
                );
        }
    }

    private void loadMyProblems() {

        if (!shouldLoadMoreProblems) {
            swipeContainer.setRefreshing(false);
            return;
        }

        Action0 onSuccess = () -> {
            Collections.sort(problemList, (lhs, rhs) -> lhs.getEntryDate().isAfter(rhs.getEntryDate()) ? -1 : 1);
            setupView();
            swipeContainer.setRefreshing(false);
            adapter.hideLoader();
            shouldLoadMoreProblems = false;
            isLoading = false;
        };

        Action1<Throwable> onError = throwable -> {
            throwable.printStackTrace();
            Toast.makeText(getContext(), R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
        };

        List<String> myProblemIds = new ArrayList<>();
        for (String key : myProblemsPreferences.getAll().keySet()) {
            myProblemIds.add(myProblemsPreferences.getString(key, ""));
        }

        Observable.from(myProblemIds)
            .map(id -> new GetProblemParams(id))
            .map(params -> new ApiRequest<>(ApiMethod.GET_REPORT, params))
            .flatMap(request -> legacyApiService.getProblem(request))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                apiResponse -> {
                    problemList.add(apiResponse.getResult());
                },
                onError,
                onSuccess
            );
    }

    @Subscribe
    public void onNewProblemAddedEvent(NewProblemAddedEvent event) {
        reloadData();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
