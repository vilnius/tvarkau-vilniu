package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_report_type_list.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.mvp.interactors.ReportTypesInteractor
import lt.vilnius.tvarkau.views.adapters.ReportTypesListAdapter
import rx.Subscription
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Martynas Jurkus
 */
class ReportTypeListFragment : BaseFragment({
    titleRes = R.string.title_choose_problem_type
    navigationMode = NavigationMode.BACK
    trackingScreenName = ActivityConstants.SCREEN_REPORT_TYPE_LIST
}), ReportTypesListAdapter.ReportTypeSelectedListener {

    private var subscription: Subscription? = null
    private var reportTypesListAdapter: ReportTypesListAdapter? = null

    @Inject
    lateinit var reportTypesInteractor: ReportTypesInteractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_report_type_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).setSupportActionBar(toolbar)
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

        subscription = reportTypesInteractor.getReportTypes()
                .doOnSubscribe { report_types_progress.visible() }
                .doOnUnsubscribe { report_types_progress.gone() }
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

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
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