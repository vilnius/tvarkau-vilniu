package lt.vilnius.tvarkau.fragments

import android.app.ProgressDialog
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
import lt.vilnius.tvarkau.extensions.emptyToNull
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_TYPE
import lt.vilnius.tvarkau.prefs.StringPreference
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

    private var subscription: Subscription? = null
    private var zoomedToMyLocation = false
    private var progressDialog: ProgressDialog? = null

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

    private fun initMapData() {
        addMultipleProblemsMarkers()
    }

    private fun addMultipleProblemsMarkers() {
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

        initMapData()
    }

    override fun onLocationInsideCity(location: Location) {
        if (!zoomedToMyLocation) {
            zoomedToMyLocation = true
            zoomToMyLocation(googleMap!!, location)
        }
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.putBoolean(EXTRA_ZOOMED_TO_MY_LOCATION, zoomedToMyLocation)
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()
        progressDialog?.dismiss()
        progressDialog = null
        super.onDestroyView()
    }

    companion object {
        private const val PROBLEM_COUNT_LIMIT_IN_MAP = 200
        private const val EXTRA_ZOOMED_TO_MY_LOCATION = "zoomed_to_my_location"

        fun newInstance() = MultipleProblemsMapFragment()
    }
}
