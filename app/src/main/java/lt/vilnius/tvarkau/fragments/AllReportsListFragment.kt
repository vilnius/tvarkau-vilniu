package lt.vilnius.tvarkau.fragments

import android.arch.lifecycle.ViewModelProvider
import android.arch.paging.PagedList
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.fab_new_report.*
import kotlinx.android.synthetic.main.include_report_list_recycler_view.*
import kotlinx.android.synthetic.main.loading_indicator.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.ReportDetailsActivity
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.observe
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.repository.NetworkState
import lt.vilnius.tvarkau.viewmodel.ReportListViewModel
import lt.vilnius.tvarkau.views.adapters.ReportListAdapter
import javax.inject.Inject

@Screen(
    titleRes = R.string.title_problem_list,
    trackingScreenName = ActivityConstants.SCREEN_ALL_REPORTS_LIST
)
class AllReportsListFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ReportListViewModel
    private var lastState: Parcelable? = null

    val adapter = ReportListAdapter(ReportDiffUtilCallback()) { view, reportId ->
        onReportClicked(reportId, view)
    }

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
            observe(networkState, ::updateNetworkState)
            observe(refreshState) {
                swipe_container.isRefreshing = it == NetworkState.LOADING
            }
        }

        swipe_container.setOnRefreshListener {
            viewModel.refresh()
        }

        report_list.adapter = adapter
        lastState = savedInstanceState?.getParcelable(STATE_LINEAR_LAYOUT)
        viewModel.getReports(savedInstanceState?.getInt(STATE_LAST_LOAD_KEY))
    }

    private fun updateNetworkState(networkState: NetworkState?) {
        //TOOD show network state in UI
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val lastKey = adapter.currentList?.lastKey as Int

        outState.putInt(STATE_LAST_LOAD_KEY, lastKey)
        outState.putParcelable(STATE_LINEAR_LAYOUT, report_list.layoutManager.onSaveInstanceState())
    }

    private fun showReports(pagedList: PagedList<ReportEntity>?) {
        loading_indicator.gone()
        adapter.submitList(pagedList)

        lastState?.let {
            report_list.layoutManager.onRestoreInstanceState(lastState)
            lastState = null
        }
    }

    private fun onReportClicked(reportId: Int, view: View) {
        val intent = ReportDetailsActivity.getStartActivityIntent(activity!!, reportId)
        val bundle = ActivityOptionsCompat.makeScaleUpAnimation(
            view,
            0,
            0,
            view.width,
            view.height
        ).toBundle()

        ActivityCompat.startActivity(context!!, intent, bundle)
    }

    fun showError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val STATE_LINEAR_LAYOUT = "linear_layout_state"
        private const val STATE_LAST_LOAD_KEY = "last_key"

        fun newInstance() = AllReportsListFragment()
    }
}
