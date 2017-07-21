package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.include_report_list_recycler_view.*
import kotlinx.android.synthetic.main.loading_indicator.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent
import lt.vilnius.tvarkau.events_listeners.RefreshReportFilterEvent
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.presenters.ProblemListPresenter
import lt.vilnius.tvarkau.fragments.views.ReportListView
import lt.vilnius.tvarkau.rx.RxBus
import lt.vilnius.tvarkau.views.adapters.ProblemsListAdapter
import rx.Subscription

/**
 * @author Martynas Jurkus
 */
abstract class BaseReportListFragment : BaseFragment(), ReportListView {

    protected val adapter by lazy { ProblemsListAdapter(activity, problemList) }
    private val problemList = ArrayList<Problem>()
    private var page = 0
    private var reloadingAllReports = false
    private var reloadReports = false
    private var subscription: Subscription? = null

    abstract val presenter: ProblemListPresenter

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_container.setOnRefreshListener { reloadData() }
        swipe_container.setColorSchemeResources(R.color.colorAccent)

        val linearLayoutManager = LinearLayoutManager(activity)
        report_list.layoutManager = linearLayoutManager
        report_list.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxBus.observable
                .filter { it is RefreshReportFilterEvent || it is NewProblemAddedEvent }
                .limit(1)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    reloadReports = true
                }).apply { subscription = this }


        if (problemList.isEmpty()) {
            getReports()
        }

        presenter.onAttach()
    }

    override fun onStart() {
        super.onStart()

        if (reloadReports) {
            reloadReports = false
            reloadData()
        }
    }

    open fun reloadData() {
        reloadingAllReports = true
        adapter.showLoader = true
        page = 0
        getReports()
    }

    open fun getReports() {
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

    override fun onReportsLoaded(reports: List<Problem>) {
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
        adapter.showLoader = false
        adapter.notifyDataSetChanged()
    }

    override fun showNetworkError(lastPage: Int) {
        //when network error - retry last request instead loosing all data
        showNoConnectionSnackbar(lastPage)
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        if (!swipe_container.isRefreshing) {
            loading_indicator.visible()
            swipe_container.gone()
        }
    }

    override fun hideProgress() {
        swipe_container.isRefreshing = false
        loading_indicator.gone()
        swipe_container.visible()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe()
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
    }

    interface OnImportReportClickListener {
        fun onImportReportClick()
    }
}