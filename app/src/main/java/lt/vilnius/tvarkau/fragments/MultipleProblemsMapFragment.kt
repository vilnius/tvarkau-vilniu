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
import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.entity.Problem
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers
import timber.log.Timber


class MultipleProblemsMapFragment : BaseMapFragment(),
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

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

        legacyApiService.getProblems(request)
                .toSingle()
                .map<List<Problem>>(Func1<ApiResponse<List<Problem>>, List<Problem>> { it.getResult() })
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
        val problemId = getProblemByMarker(marker).getId()

        val intent = ProblemDetailActivity.getStartActivityIntent(activity, problemId)

        startActivity(intent)
    }

    override fun onInfoWindowClose(marker: Marker) {
        val problem = getProblemByMarker(marker)

        activity.setTitle(R.string.title_problems_map)

        marker.setIcon(getMarkerIcon(problem))
    }

    override fun onMapReady(map: GoogleMap) {
        super.onMapReady(map)

        map.setOnInfoWindowClickListener(this)
        map.setOnInfoWindowCloseListener(this)
    }

    override fun onLocationInsideCity(location: Location) {
        zoomToMyLocation(googleMap!!, location)
    }

    companion object {

        private val PROBLEM_COUNT_LIMIT_IN_MAP = 200

        val instance: MultipleProblemsMapFragment
            get() = MultipleProblemsMapFragment()
    }
}
