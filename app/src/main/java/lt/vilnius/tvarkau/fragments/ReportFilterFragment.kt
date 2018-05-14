package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_map_report_filter.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.entity.Problem.Companion.STATUS_DONE
import lt.vilnius.tvarkau.entity.Problem.Companion.STATUS_REGISTERED
import lt.vilnius.tvarkau.events_listeners.RefreshReportFilterEvent
import lt.vilnius.tvarkau.extensions.emptyToNull
import lt.vilnius.tvarkau.mvp.interactors.ReportTypesInteractor
import lt.vilnius.tvarkau.rx.RxBus
import lt.vilnius.tvarkau.views.adapters.FilterReportTypesAdapter
import javax.inject.Inject

/**
 * @author Martynas Jurkus
 */
@Screen(titleRes = R.string.report_filter_page_title,
        navigationMode = NavigationMode.CLOSE,
        trackingScreenName = ActivityConstants.SCREEN_REPORT_FILTER)
class ReportFilterFragment : BaseFragment() {

    @Inject
    lateinit var reportTypesInteractor: ReportTypesInteractor

    private lateinit var adapter: FilterReportTypesAdapter

    private val reportTypes = mutableListOf<String>()

    private val isMapTarget: Boolean
        get() = arguments!!.getInt(KEY_TARGET) == TARGET_MAP

    private val allReportTypesLabel: String
        get() = getString(R.string.report_filter_all_report_types)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_report_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        reportTypes += allReportTypesLabel

        filter_report_status_new.tag = null
        filter_report_status_registered.tag = STATUS_REGISTERED
        filter_report_status_completed.tag = STATUS_DONE

        val onSelectState: (View) -> Unit = {
            unSelectAllStates()
            it.isSelected = true
        }

        filter_report_status_new.setOnClickListener { onSelectState(it) }
        filter_report_status_registered.setOnClickListener { onSelectState(it) }
        filter_report_status_completed.setOnClickListener { onSelectState(it) }
    }

    private fun unSelectAllStates() {
        filter_report_status_new.isSelected = false
        filter_report_status_registered.isSelected = false
        filter_report_status_completed.isSelected = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val previouslySelectedStatus = getSelectedReportStatus()
        if (previouslySelectedStatus.isEmpty()) {
            filter_report_status_new.isSelected = true
        } else {
            filter_report_status_registered.isSelected = previouslySelectedStatus == STATUS_REGISTERED
            filter_report_status_completed.isSelected = previouslySelectedStatus == STATUS_DONE
        }

        adapter = FilterReportTypesAdapter(
                reportTypes,
                getSelectedReportType(),
                {
                    adapter.selected = it
                    adapter.notifyDataSetChanged()
                }
        )
        filter_report_types.adapter = adapter

        connectivityProvider.ensureConnected()
                .flatMap { reportTypesInteractor.getReportTypes() }
                .observeOn(uiScheduler)
                .subscribe({
                    reportTypes.addAll(it)
                    adapter.notifyDataSetChanged()
                }, {
                    Toast.makeText(context, R.string.error_network_generic, Toast.LENGTH_SHORT).show()
                    activity!!.onBackPressed()
                })
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.submit_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_send -> {
                onSubmitFilter()
                activity!!.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getSelectedReportType(): String {
        val selected = if (isMapTarget) {
            appPreferences.reportTypeSelectedFilter.get()
        } else {
            appPreferences.reportTypeSelectedListFilter.get()
        }

        return selected.emptyToNull() ?: allReportTypesLabel
    }

    private fun getSelectedReportStatus(): String {
        return if (isMapTarget) {
            appPreferences.reportStatusSelectedListFilter.get()
        } else {
            appPreferences.reportStatusSelectedListFilter.get()
        }
    }

    private fun onSubmitFilter() {
        val selectedState: String? = listOf(
                filter_report_status_new,
                filter_report_status_registered,
                filter_report_status_completed
        ).find { it?.isSelected ?: false }?.tag as? String

        val targetName = if (isMapTarget) {
            appPreferences.reportStatusSelectedListFilter.set(selectedState.orEmpty())
            appPreferences.reportTypeSelectedFilter.set(adapter.selected)
            "map"
        } else {
            appPreferences.reportStatusSelectedListFilter.set(selectedState.orEmpty())
            appPreferences.reportTypeSelectedListFilter.set(adapter.selected)
            "list"
        }

        analytics.trackApplyReportFilter(selectedState.orEmpty(), adapter.selected, targetName)

        RxBus.publish(RefreshReportFilterEvent())
    }

    companion object {
        const val TARGET_MAP = 1
        const val TARGET_LIST = 2

        private const val KEY_TARGET = "target"

        fun newInstance(target: Int): ReportFilterFragment {
            return ReportFilterFragment().apply {
                arguments = Bundle()
                arguments!!.putInt(KEY_TARGET, target)
            }
        }
    }
}