package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fab_new_report.*
import kotlinx.android.synthetic.main.fragment_my_reports_list.*
import kotlinx.android.synthetic.main.include_report_list_recycler_view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.observe
import lt.vilnius.tvarkau.extensions.observeNonNull
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.extensions.visibleIf
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.repository.NetworkState
import lt.vilnius.tvarkau.viewmodel.MyReportListViewModel
import javax.inject.Inject

@Screen(
    titleRes = R.string.title_my_problem_list,
    trackingScreenName = ActivityConstants.SCREEN_MY_REPORTS_LIST
)
class MyReportsListFragment : BaseReportListFragment() {

    @Inject
    lateinit var navigationManager: NavigationManager

    private lateinit var viewModel: MyReportListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = withViewModel(viewModelFactory) {
            observeNonNull(errorEvents, ::showError)
            observe(reports, ::showReports)
            observeNonNull(networkState, ::updateNetworkState)
            observe(refreshState) {
                swipe_container.isRefreshing = it == NetworkState.LOADING
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_reports_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        my_problems_import.setOnClickListener { onImportReportClick() }
        fab_report.setOnClickListener { navigationManager.navigateToNewReport() }
        swipe_container.setOnRefreshListener { viewModel.refresh() }
        swipe_container.visible()
    }

    private fun onImportReportClick() {
        navigationManager.showReportsImportDialog()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getReports()
    }

    private fun updateNetworkState(networkState: NetworkState) {
        when (networkState) {
            NetworkState.LOADING -> {
                my_problems_empty_view.gone()
                if (adapter.itemCount == 0) {
                    swipe_container.isRefreshing = true
                }
            }
            NetworkState.LOADED -> {
                my_problems_empty_view.visibleIf(adapter.itemCount == 0)
                swipe_container.isRefreshing = false
            }
        }
    }

    companion object {
        fun newInstance() = MyReportsListFragment()
    }
}
