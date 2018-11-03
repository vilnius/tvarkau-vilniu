package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fab_new_report.*
import kotlinx.android.synthetic.main.include_report_list_recycler_view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.events_listeners.RefreshReportFilterEvent
import lt.vilnius.tvarkau.extensions.observe
import lt.vilnius.tvarkau.extensions.observeNonNull
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.repository.NetworkState
import lt.vilnius.tvarkau.viewmodel.ReportListViewModel
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@Screen(
    titleRes = R.string.title_problem_list,
    trackingScreenName = ActivityConstants.SCREEN_ALL_REPORTS_LIST
)
class AllReportsListFragment : BaseReportListFragment() {

    @Inject
    lateinit var navigationManager: NavigationManager

    private lateinit var viewModel: ReportListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_reports_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_container.visible()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fab_report.setOnClickListener { navigationManager.navigateToNewReport() }

        viewModel = withViewModel(viewModelFactory) {
            observe(reports, ::showReports)
            observeNonNull(networkState, ::updateNetworkState)
            observe(refreshState) {
                swipe_container.isRefreshing = it == NetworkState.LOADING
            }
            observeNonNull(errorEvents, ::showError)
        }

        swipe_container.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.getReports()
    }

    @Subscribe
    fun onFilterParamsChanged(event: RefreshReportFilterEvent) {
        viewModel.refresh()
    }

    private fun updateNetworkState(networkState: NetworkState) {
        when (networkState) {
            NetworkState.LOADING -> {
                if (adapter.itemCount == 0) {
                    swipe_container.isRefreshing = true
                }
            }
            NetworkState.LOADED -> {
                swipe_container.isRefreshing = false
            }
        }
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

    companion object {
        fun newInstance() = AllReportsListFragment()
    }
}
