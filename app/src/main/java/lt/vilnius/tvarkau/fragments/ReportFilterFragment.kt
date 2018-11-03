package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_map_report_filter.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.extensions.observeNonNull
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.viewmodel.ReportFilterViewModel
import lt.vilnius.tvarkau.viewmodel.ReportFilterViewModel.ReportStatusViewEntity
import lt.vilnius.tvarkau.viewmodel.ReportFilterViewModel.ReportTypeViewEntity
import lt.vilnius.tvarkau.views.adapters.FilterReportTypesAdapter

@Screen(
    titleRes = R.string.report_filter_page_title,
    navigationMode = NavigationMode.CLOSE,
    trackingScreenName = ActivityConstants.SCREEN_REPORT_FILTER
)
class ReportFilterFragment : BaseFragment() {

    private lateinit var adapter: FilterReportTypesAdapter
    private lateinit var viewModel: ReportFilterViewModel

    private val isMapTarget: Boolean
        get() = arguments!!.getInt(KEY_TARGET) == TARGET_MAP

    private val allReportTypesLabel: String
        get() = getString(R.string.report_filter_all_report_types)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = withViewModel(viewModelFactory) {
            observeNonNull(reportTypes, ::updateReportTypes)
            observeNonNull(reportStatuses, ::updateReportStates)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_report_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun updateReportTypes(reportTypes: List<ReportTypeViewEntity>) {
        adapter.showReportTypes(reportTypes)
    }

    private fun updateReportStates(list: List<ReportStatusViewEntity>) {
        (0 until filter_report_status_container.childCount).forEach { idx ->
            val child = filter_report_status_container.getChildAt(idx) as TextView
            val entity = list[idx]
            child.text = entity.reportStatus.title
            child.isSelected = entity.selected
            child.setOnClickListener {
                viewModel.onReportStatusSelected(entity.reportStatus)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.initWith(allReportTypesLabel, isMapTarget)

        adapter = FilterReportTypesAdapter(viewModel::onReportTypeSelected)
        filter_report_types.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.submit_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_send -> {
                onSubmitFilter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onSubmitFilter() {
        viewModel.onSubmit()
        activity!!.onBackPressed()
    }

    companion object {
        const val TARGET_MAP = 1
        const val TARGET_LIST = 2

        private const val KEY_TARGET = "target"

        fun newInstance(target: Int): ReportFilterFragment {
            return ReportFilterFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_TARGET, target)
                }
            }
        }
    }
}
