package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.no_internet.*
import kotlinx.android.synthetic.main.problem_list.*
import kotlinx.android.synthetic.main.server_not_responding.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.EndlessRecyclerViewScrollListener
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.AllReportListInteractor
import lt.vilnius.tvarkau.fragments.interactors.MyReportListInteractor
import lt.vilnius.tvarkau.fragments.interactors.SharedPreferencesMyReportsInteractor
import lt.vilnius.tvarkau.fragments.presenters.AllReportsListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProviderImpl
import lt.vilnius.tvarkau.fragments.presenters.MyReportListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.ProblemListPresenter
import lt.vilnius.tvarkau.fragments.views.ReportListView
import lt.vilnius.tvarkau.views.adapters.ProblemsListAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class ProblemsListFragment : BaseFragment(), ReportListView {

    private val problemList = ArrayList<Problem>()
    private val adapter by lazy { ProblemsListAdapter(activity, problemList) }

    private val isAllProblemList: Boolean
        get() = arguments.getBoolean(ALL_PROBLEM_LIST)

    private var shouldLoadMoreProblems = true
    private var isLoading = false
    private var reloadingAllReports = false

    private val presenter: ProblemListPresenter by lazy {
        if (isAllProblemList) {
            AllReportsListPresenterImpl(
                    AllReportListInteractor(
                            legacyApiService,
                            ioScheduler
                    ),
                    uiScheduler,
                    this,
                    ConnectivityProviderImpl(activity)
            )
        } else {
            MyReportListPresenterImpl(
                    MyReportListInteractor(
                            legacyApiService,
                            SharedPreferencesMyReportsInteractor(myProblemsPreferences),
                            ioScheduler
                    ),
                    uiScheduler,
                    this,
                    ConnectivityProviderImpl(activity)
            )
        }
    }

    interface OnImportReportClickListener {
        fun onImportReportClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.problem_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_container.setOnRefreshListener { reloadData() }
        swipe_container.setColorSchemeResources(R.color.colorAccent)

        val linearLayoutManager = LinearLayoutManager(activity)
        problem_list.layoutManager = linearLayoutManager
        problem_list.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                //Scroll listener always starts from page 1
                getReports(page - 1)
            }
        })

        problem_list.adapter = adapter

        no_internet_view.gone()
        server_not_responding_view.gone()

        my_problems_import.setOnClickListener {
            val listener = activity as OnImportReportClickListener
            listener.onImportReportClick()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (problemList.isEmpty() && shouldLoadMoreProblems) {
            getReports(page = 0)
        }

        presenter.onAttach()
    }

    override fun onReportsLoaded(reports: List<Problem>) {
        swipe_container.isRefreshing = false

        if (reloadingAllReports) {
            problemList.clear()
            problemList.addAll(reports)
            adapter.notifyDataSetChanged()
        } else {
            problemList.addAll(reports)
            adapter.notifyItemRangeInserted(problemList.size - 1, reports.size)
        }
    }

    override fun hideLoader() {
        adapter.hideLoader()
        adapter.notifyDataSetChanged()
    }

    override fun showEmptyState() {
        my_problems_empty_view.visible()
    }

    override fun hideEmptyState() {
        my_problems_empty_view.gone()
    }

    override fun markLoading(isLoading: Boolean) {
        this.isLoading = isLoading

        if (!isLoading) {
            swipe_container.isRefreshing = false
        }
    }

    override fun showNetworkError(lastPage: Int) {
        //when network error - retry last request instead loosing all data
        showNoConnectionSnackbar(lastPage)
    }

    override fun showError(error: Throwable) {
        //TODO show some other error - how?
    }

    private fun reloadData() {
        reloadingAllReports = true
        getReports(0)
    }

    private fun getReports(page: Int) {
        if (isLoading) return

        presenter.getReportsForPage(page)
    }

    private fun showNoConnectionSnackbar(lastPage: Int) {
        Snackbar.make(activity.findViewById(R.id.coordinator_layout), R.string.no_connection, Snackbar
                .LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(context, R.color.snackbar_action_text))
                .setAction(R.string.try_again) { getReports(page = lastPage) }
                .show()
    }

    @Subscribe
    fun onNewProblemAddedEvent(event: NewProblemAddedEvent) {
        shouldLoadMoreProblems = true
        isLoading = false
        reloadData()
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {

        private val ALL_PROBLEM_LIST = "all_problem_list"

        fun allReportsListInstance(): ProblemsListFragment {
            return ProblemsListFragment().apply {
                arguments = Bundle()
                arguments.putBoolean(ALL_PROBLEM_LIST, true)
            }
        }

        fun myReportsListInstance(): ProblemsListFragment {
            return ProblemsListFragment().apply {
                arguments = Bundle()
                arguments.putBoolean(ALL_PROBLEM_LIST, false)
            }
        }
    }
}
