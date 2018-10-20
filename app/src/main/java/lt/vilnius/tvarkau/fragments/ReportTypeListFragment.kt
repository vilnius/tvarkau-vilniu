package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_report_type_list.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.extensions.observeNonNull
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.viewmodel.ReportTypeListViewModel
import lt.vilnius.tvarkau.views.adapters.ReportTypesListAdapter

@Screen(titleRes = R.string.title_choose_problem_type,
        navigationMode = NavigationMode.BACK,
        trackingScreenName = ActivityConstants.SCREEN_REPORT_TYPE_LIST)
class ReportTypeListFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_report_type_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).setSupportActionBar(toolbar)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                android.R.id.home -> {
                    activity!!.onBackPressed()
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel<ReportTypeListViewModel>(viewModelFactory) {
            observeNonNull(reportTypes, ::updateReportTypes)
        }
    }

    private fun updateReportTypes(reportTypes: List<ReportType>) {
        report_types_recycler_view.adapter = ReportTypesListAdapter(reportTypes) {
            onReportTypeSelected(it)
        }
    }

    private fun onReportTypeSelected(reportType: ReportType) {
        (activity!! as ReportRegistrationActivity).onTypeSelected(reportType)
    }

    companion object {
        fun newInstance() = ReportTypeListFragment()
    }
}
