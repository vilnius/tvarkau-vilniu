package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_map_report_filter.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.entity.Problem.Companion.STATUS_DONE
import lt.vilnius.tvarkau.entity.Problem.Companion.STATUS_REGISTERED
import lt.vilnius.tvarkau.extensions.emptyToNull
import lt.vilnius.tvarkau.extensions.nullToEmpty
import lt.vilnius.tvarkau.mvp.interactors.ReportTypesInteractor
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_TYPE
import lt.vilnius.tvarkau.prefs.StringPreference
import lt.vilnius.tvarkau.views.adapters.FilterReportTypesAdapter
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */
class ReportFilterFragment : BaseFragment() {

    @Inject
    lateinit var reportTypesInteractor: ReportTypesInteractor
    @field:[Inject Named(SELECTED_FILTER_REPORT_STATUS)]
    lateinit var reportStatusFilter: StringPreference
    @field:[Inject Named(SELECTED_FILTER_REPORT_TYPE)]
    lateinit var reportTypeFilter: StringPreference

    private lateinit var adapter: FilterReportTypesAdapter

    private val reportTypes = mutableListOf<String>()

    private val allReportTypesLabel: String
        get() = getString(R.string.report_filter_all_report_types)

    private val selectedReportType: String
        get() = reportTypeFilter.get().emptyToNull() ?: allReportTypesLabel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_report_filter, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
        activity.title = getString(R.string.report_filter_page_title)

        val previouslySelectedStatus = reportStatusFilter.get()
        if (previouslySelectedStatus.isNullOrEmpty()) {
            filter_report_status_new.isSelected = true
        } else {
            filter_report_status_registered.isSelected = previouslySelectedStatus == STATUS_REGISTERED
            filter_report_status_completed.isSelected = previouslySelectedStatus == STATUS_DONE
        }

        adapter = FilterReportTypesAdapter(
                reportTypes,
                selectedReportType,
                {
                    reportTypeFilter.set(it)
                    adapter.selected = it
                    adapter.notifyDataSetChanged()
                }
        )
        filter_report_types.adapter = adapter

        reportTypesInteractor.getReportTypes()
                .observeOn(uiScheduler)
                .subscribe({
                    reportTypes.addAll(it)
                    adapter.notifyDataSetChanged()
                }, {

                })
    }

    override fun onInject(component: ApplicationComponent) {
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onSubmitFilter() {
        val selectedState: String? = listOf(
                filter_report_status_new,
                filter_report_status_registered,
                filter_report_status_completed
        ).find { it.isSelected }?.tag as? String

        reportStatusFilter.set(selectedState.nullToEmpty())

        (activity as FilterSubmitListener).filterSubmitted()
    }

    companion object {
        fun newInstance() = ReportFilterFragment()
    }

    interface FilterSubmitListener {
        fun filterSubmitted()
    }
}