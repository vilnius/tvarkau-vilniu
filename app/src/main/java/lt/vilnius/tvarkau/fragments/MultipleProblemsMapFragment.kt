package lt.vilnius.tvarkau.fragments

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import lt.vilnius.tvarkau.ProblemDetailActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.backend.ApiMethod
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.entity.Problem
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers
import timber.log.Timber


class MultipleProblemsMapFragment : BaseMapFragment(),
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    private var subscription: Subscription? = null
    private var zoomedToMyLocation = false

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        if (bundle != null) {
            zoomedToMyLocation = bundle.getBoolean(EXTRA_ZOOMED_TO_MY_LOCATION)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getMapAsync(this)
    }

    override fun initMapData() {
        addMultipleProblemsMarkers()
    }

    private fun addMultipleProblemsMarkers() {
        val params = GetProblemsParams.Builder()
                .setStart(0)
                .setLimit(PROBLEM_COUNT_LIMIT_IN_MAP)
                .setDescriptionFilter(null)
                .setTypeFilter(null)
                .setAddressFilter(null)
                .setReporterFilter(null)
                .setDateFilter(null)
                .setStatusFilter(null)
                .create()
        val request = ApiRequest(ApiMethod.GET_PROBLEMS, params)

        val onSuccess = Action1<List<Problem>> { this.populateMarkers(it) }

        val onError = Action1<Throwable> {
            throwable: Throwable ->
            Toast.makeText(context, R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show()
            Timber.e(throwable)
        }

        subscription = legacyApiService.getProblems(request)
                .toSingle()
                .map { it.result }
                .doOnSuccess { problems ->
                    if (problems.isEmpty()) {
                        throw IllegalStateException("Empty problem list returned")
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        onError
                )
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

    override fun onMapReady(map: GoogleMap) {
        super.onMapReady(map)

        map.setOnInfoWindowClickListener(this)
        map.setOnInfoWindowCloseListener(this)
    }

    override fun onLocationInsideCity(location: Location) {
        if (!zoomedToMyLocation) {
            zoomedToMyLocation = true;
            zoomToMyLocation(googleMap!!, location)
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putBoolean(EXTRA_ZOOMED_TO_MY_LOCATION, zoomedToMyLocation)

        super.onSaveInstanceState(bundle)
    }


    override fun onDestroyView() {
        subscription?.unsubscribe()
        super.onDestroyView()
    }

    companion object {

        private val PROBLEM_COUNT_LIMIT_IN_MAP = 200

        private val EXTRA_ZOOMED_TO_MY_LOCATION = "zoomed_to_my_location";

        val instance: MultipleProblemsMapFragment
            get() = MultipleProblemsMapFragment()
    }
}
