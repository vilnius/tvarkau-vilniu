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

    private static final int PROBLEM_COUNT_LIMIT_PER_PAGE = 100;
    private static final String ALL_PROBLEM_LIST = "all_problem_list";
    private List<Problem> problemList;
    private ProblemsListAdapter adapter;
    private Unbinder unbinder;
    private CompositeSubscription subscriptions;
    private Boolean isAllProblemList;
    private int myProblemsCount;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.problem_list, container, false);

        unbinder = ButterKnife.bind(this, view);

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

        if (isAllProblemList) {

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
                if (apiResponse.getResult().size() > 0) {
                    problemList.addAll(apiResponse.getResult());
                    setupView();
                    swipeContainer.setRefreshing(false);
                } else {
                    Toast.makeText(getContext(), R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show();
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
        } else {
            problemList.clear();
            myProblemsCount = 0;

            Action0 onSuccess = () -> {
                if (problemList.size() == myProblemsCount) {
                    Collections.sort(problemList, (lhs, rhs) -> lhs.getEntryDate().isAfter(rhs.getEntryDate()) ? -1 : 1);
                    setupView();
                    swipeContainer.setRefreshing(false);
                }
            };

            Action1<Throwable> onError = throwable -> {
                throwable.printStackTrace();
                Toast.makeText(getContext(), R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
            };

            for (String key : myProblemsPreferences.getAll().keySet()) {
                myProblemsCount++;
                String issueId = myProblemsPreferences.getString(key, "");
                GetProblemParams params = new GetProblemParams(issueId);
                ApiRequest<GetProblemParams> request = new ApiRequest<>(ApiMethod.GET_REPORT, params);

                legacyApiService.getProblem(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        apiResponse -> problemList.add(apiResponse.getResult()),
                        onError,
                        onSuccess
                    );
            }

            if (myProblemsCount == 0) {
                swipeContainer.setRefreshing(false);
            }
        }
    }

    @Subscribe
    public void onNewProblemAddedEvent(NewProblemAddedEvent event) {
        getData(0);
    }

    @Override
    public void onResume() {
        getData(0);
        super.onResume();
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
