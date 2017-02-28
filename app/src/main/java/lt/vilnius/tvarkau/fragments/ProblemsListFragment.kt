package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.no_internet.*
import kotlinx.android.synthetic.main.problem_list.*
import kotlinx.android.synthetic.main.server_not_responding.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent
import lt.vilnius.tvarkau.events_listeners.RefreshReportFilterEvent
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.AllReportListInteractor
import lt.vilnius.tvarkau.fragments.interactors.MyReportListInteractor
import lt.vilnius.tvarkau.fragments.interactors.SharedPreferencesMyReportsInteractor
import lt.vilnius.tvarkau.fragments.presenters.AllReportsListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.MyReportListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.ProblemListPresenter
import lt.vilnius.tvarkau.fragments.views.ReportListView
import lt.vilnius.tvarkau.prefs.Preferences
import lt.vilnius.tvarkau.prefs.StringPreference
import lt.vilnius.tvarkau.rx.RxBus
import lt.vilnius.tvarkau.views.adapters.ProblemsListAdapter
import lt.vilnius.tvarkau.widgets.EndlessScrollListener
import rx.Subscription
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ProblemsListFragment : BaseFragment(), ReportListView {

    @field:[Inject Named(Preferences.LIST_SELECTED_FILTER_REPORT_STATUS)]
    lateinit var reportStatus: StringPreference
    @field:[Inject Named(Preferences.LIST_SELECTED_FILTER_REPORT_TYPE)]
    lateinit var reportType: StringPreference

    private val problemList = ArrayList<Problem>()
    private val adapter by lazy { ProblemsListAdapter(activity, problemList) }

    private val isAllProblemList: Boolean
        get() = arguments.getBoolean(ALL_PROBLEM_LIST)

    private var reloadingAllReports = false
    private var page = 0
    private var subscription: Subscription? = null
    private lateinit var scrollListener: EndlessScrollListener

    private val presenter: ProblemListPresenter by lazy {
        if (isAllProblemList) {
            AllReportsListPresenterImpl(
                    AllReportListInteractor(
                            legacyApiService,
                            ioScheduler,
                            reportType,
                            reportStatus,
                            getString(R.string.report_filter_all_report_types)
                    ),
                    uiScheduler,
                    this,
                    connectivityProvider
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
                    connectivityProvider
            )
        }
    }

    interface OnImportReportClickListener {
        fun onImportReportClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        scrollListener = EndlessScrollListener({ getReports() })
        problem_list.addOnScrollListener(scrollListener)
        //scroll listener should load only where pagination is possible
        scrollListener.isEnabled = isAllProblemList

        problem_list.adapter = adapter

        no_internet_view.gone()
        server_not_responding_view.gone()

        my_problems_import.setOnClickListener {
            val listener = activity as OnImportReportClickListener
            listener.onImportReportClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (isAllProblemList) {
            inflater.inflate(R.menu.main_toolbar_menu, menu)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val title = if (isAllProblemList) R.string.home_list_of_reports else R.string.home_my_problems
        baseActivity?.setTitle(title)

        RxBus.observable
                .filter { it is RefreshReportFilterEvent || it is NewProblemAddedEvent }
                .observeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    reloadData()
                }).apply { subscription = this }

        if (problemList.isEmpty()) {
            getReports()
        }

        presenter.onAttach()
    }

    override fun onInject(component: ApplicationComponent) {
        component.inject(this)
    }

    override fun onReportsLoaded(reports: List<Problem>) {
        swipe_container.isRefreshing = false

        if (reloadingAllReports) {
            reloadingAllReports = false
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
        scrollListener.isLoading = isLoading

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
        scrollListener.isLoading = false
        reloadingAllReports = true
        page = 0
        getReports()
    }

    private fun getReports() {
        if (scrollListener.isLoading) return
        scrollListener.isLoading = true

        presenter.getReportsForPage(page)
        page++
    }

    private fun showNoConnectionSnackbar(lastPage: Int) {
        Snackbar.make(activity.findViewById(R.id.coordinator_layout), R.string.no_connection, Snackbar
                .LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(context, R.color.snackbar_action_text))
                .setAction(R.string.try_again) {
                    page = lastPage
                    getReports()
                }
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe()
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
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
