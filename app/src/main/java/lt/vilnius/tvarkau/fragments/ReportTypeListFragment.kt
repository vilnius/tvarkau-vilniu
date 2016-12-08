package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.vinted.extensions.gone
import com.vinted.extensions.visible
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_report_type_list.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.backend.ApiMethod
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetProblemTypesParams
import lt.vilnius.tvarkau.views.adapters.ReportTypesListAdapter
import rx.Subscription
import timber.log.Timber

/**
 * @author Martynas Jurkus
 */
class ReportTypeListFragment : BaseFragment(), ReportTypesListAdapter.ReportTypeSelectedListener {

    private var subscription: Subscription? = null
    private var reportTypesListAdapter: ReportTypesListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_report_type_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(activity as BaseActivity) {
            setSupportActionBar(toolbar)

            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setTitle(R.string.title_choose_problem_type)
            }
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                android.R.id.home -> {
                    activity.onBackPressed()
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val request = ApiRequest<GetProblemTypesParams>(ApiMethod.GET_PROBLEM_TYPES, null)

        legacyApiService.getProblemTypes(request)
                .toSingle()
                .doOnSubscribe { report_types_progress.visible() }
                .doOnUnsubscribe { report_types_progress.gone() }
                .map { it.result }
                .doOnSuccess { if (it == null || it.isEmpty()) throw IllegalStateException("No report types to display") }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    reportTypesListAdapter = ReportTypesListAdapter(this@ReportTypeListFragment, it, context)
                    report_types_recycler_view.adapter = reportTypesListAdapter
                }, {
                    Timber.e(it)
                    Toast.makeText(context, R.string.error_loading_report_types, Toast.LENGTH_SHORT).show()
                })
                .apply { subscription = this }
    }

    override fun onReportTypeSelected(reportType: String) {
        (activity as ReportRegistrationActivity).onTypeSelected(reportType)
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()
        reportTypesListAdapter?.setReportTypeSelectedListener(null)
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = ReportTypeListFragment()
    }
}