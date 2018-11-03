package lt.vilnius.tvarkau.fragments

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.include_report_list_recycler_view.*
import kotlinx.android.synthetic.main.loading_indicator.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.ReportDetailsActivity
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.views.adapters.ReportListAdapter

abstract class BaseReportListFragment : BaseFragment() {

    val adapter = ReportListAdapter(ReportDiffUtilCallback()) { view, reportId ->
        onReportClicked(reportId, view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_container.setColorSchemeResources(R.color.colorAccent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        report_list.adapter = adapter
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

    protected fun showReports(pagedList: PagedList<ReportEntity>?) {
        loading_indicator.gone()
        adapter.submitList(pagedList)
    }

    protected fun showError(error: Throwable) {
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
    }
}
