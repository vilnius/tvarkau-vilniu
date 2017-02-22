package lt.vilnius.tvarkau.fragments

import android.app.ProgressDialog
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import lt.vilnius.tvarkau.ProblemDetailActivity
import lt.vilnius.tvarkau.ProblemsMapActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.backend.requests.GetReportListRequest
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.RefreshMapEvent
import lt.vilnius.tvarkau.extensions.emptyToNull
import lt.vilnius.tvarkau.prefs.BooleanPreference
import lt.vilnius.tvarkau.prefs.Preferences.FILTER_UPDATED
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_TYPE
import lt.vilnius.tvarkau.prefs.StringPreference
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import rx.Subscription
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class MultipleProblemsMapFragment : BaseMapFragment(),
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    @field:[Inject Named(SELECTED_FILTER_REPORT_STATUS)]
    lateinit var reportStatusFilter: StringPreference
    @field:[Inject Named(SELECTED_FILTER_REPORT_TYPE)]
    lateinit var reportTypeFilter: StringPreference
    @field:[Inject Named(FILTER_UPDATED)]
    lateinit var filterUpdated: BooleanPreference

    private var subscription: Subscription? = null
    private var zoomedToMyLocation = false
    private var progressDialog: ProgressDialog? = null
    private var latestReports = emptyList<Problem>()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        bundle?.let {
            zoomedToMyLocation = it.getBoolean(EXTRA_ZOOMED_TO_MY_LOCATION)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.setTitle(R.string.title_problems_map)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onInject(component: ApplicationComponent) {
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.map_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_filter -> {
                (activity as ProblemsMapActivity).openFilters()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addMarkers() {
        val mappedStatus = reportStatusFilter.get().emptyToNull()

        val mappedType = when (reportTypeFilter.get()) {
            getString(R.string.report_filter_all_report_types) -> null
            else -> reportTypeFilter.get().emptyToNull()
        }

        val params = GetProblemsParams.Builder()
                .setStart(0)
                .setLimit(PROBLEM_COUNT_LIMIT_IN_MAP)
                .setTypeFilter(mappedType)
                .setStatusFilter(mappedStatus)
                .create()

        val request = GetReportListRequest(params)

        subscription = legacyApiService.getProblems(request)
                .toSingle()
                .map { it.result }
                .doOnSuccess { reports ->
                    if (reports.isEmpty()) {
                        throw IllegalStateException("Empty problem list returned")
                    }
                }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { showProgress() }
                .doOnUnsubscribe { hideProgress() }
                .subscribe({
                    latestReports = it
                    populateMarkers(it)
                }, {
                    Toast.makeText(context, R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show()
                    Timber.e(it)
                })
    }

    private fun showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context).apply {
                setMessage(getString(R.string.multiple_reports_map_message_progress))
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                setCancelable(false)
            }
        }

        progressDialog?.show()
    }

    private fun hideProgress() {
        progressDialog?.dismiss()
    }

    override fun onInfoWindowClick(marker: Marker) {
        val problemId = (marker.tag as Problem).id
        val intent = ProblemDetailActivity.getStartActivityIntent(activity, problemId)
        startActivity(intent)
    }

    override fun onInfoWindowClose(marker: Marker) {
        val problem = marker.tag as Problem

        activity.setTitle(R.string.title_problems_map)

        marker.setIcon(getMarkerIcon(problem))
    }

    override fun onMapLoaded() {
        super.onMapLoaded()

        googleMap?.setOnInfoWindowClickListener(this)
        googleMap?.setOnInfoWindowCloseListener(this)

        if (latestReports.isEmpty()) {
            addMarkers()
        } else {
            populateMarkers(latestReports)
        }
    }

    override fun onLocationInsideCity(location: Location) {
        if (!zoomedToMyLocation) {
            zoomedToMyLocation = true
            zoomToMyLocation(googleMap!!, location)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_ZOOMED_TO_MY_LOCATION, zoomedToMyLocation)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

    override fun onDestroyView() {
        googleMap?.setOnInfoWindowCloseListener(null)
        googleMap?.setOnInfoWindowClickListener(null)
        subscription?.unsubscribe()
        progressDialog?.dismiss()
        progressDialog = null
        super.onDestroyView()
    }

    @Subscribe
    fun onRefreshMapEvent(event: RefreshMapEvent) {
        latestReports = emptyList<Problem>()
    }

    companion object {
        private const val PROBLEM_COUNT_LIMIT_IN_MAP = 200
        private const val EXTRA_ZOOMED_TO_MY_LOCATION = "zoomed_to_my_location"

        fun newInstance() = MultipleProblemsMapFragment()
    }
}
