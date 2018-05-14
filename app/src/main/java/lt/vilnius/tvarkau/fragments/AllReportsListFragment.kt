package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fab_new_report.*
import kotlinx.android.synthetic.main.fragment_all_reports_list.*
import kotlinx.android.synthetic.main.include_report_list_recycler_view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.AllReportListInteractor
import lt.vilnius.tvarkau.fragments.presenters.AllReportsListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.ProblemListPresenter
import lt.vilnius.tvarkau.fragments.views.ReportListView
import lt.vilnius.tvarkau.widgets.EndlessScrollListener

/**
 * @author Martynas Jurkus
 */
@Screen(titleRes = R.string.title_problem_list,
        trackingScreenName = ActivityConstants.SCREEN_ALL_REPORTS_LIST)
class AllReportsListFragment : BaseReportListFragment(), ReportListView {

    private lateinit var scrollListener: EndlessScrollListener

    override val presenter: ProblemListPresenter by lazy {
        AllReportsListPresenterImpl(
                AllReportListInteractor(
                        legacyApiService,
                        ioScheduler,
                        appPreferences.reportTypeSelectedListFilter,
                        appPreferences.reportStatusSelectedListFilter,
                        getString(R.string.report_filter_all_report_types)
                ),
                uiScheduler,
                this,
                connectivityProvider
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_reports_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollListener = EndlessScrollListener({ getReports() })
        report_list.addOnScrollListener(scrollListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fab_report.setOnClickListener { navigationManager.navigateToNewReport() }
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.report_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_filter -> {
                navigationManager.navigateToReportsListFilter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun getReports() {
        if (scrollListener.isLoading) return
        scrollListener.isLoading = true
        super.getReports()
    }

    override fun reloadData() {
        super.reloadData()
        scrollListener.isLoading = false
    }

    override fun onReportsLoaded(reports: List<Problem>) {
        super.onReportsLoaded(reports)
        swipe_container.isRefreshing = false
    }

    override fun showProgress() {
        super.showProgress()
        scrollListener.isLoading = true
    }

    override fun hideProgress() {
        super.hideProgress()
        scrollListener.isLoading = false
    }

    override fun showEmptyState() {
        all_reports_empty_state.visible()
    }

    override fun hideEmptyState() {
        all_reports_empty_state.gone()
    }

    companion object {
        fun newInstance() = AllReportsListFragment()
    }
}