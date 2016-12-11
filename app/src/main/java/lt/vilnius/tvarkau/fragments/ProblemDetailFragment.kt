package lt.vilnius.tvarkau.fragments

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.vinted.extensions.gone
import com.vinted.extensions.goneIf
import com.vinted.extensions.visible
import kotlinx.android.synthetic.main.no_internet.*
import kotlinx.android.synthetic.main.problem_detail.*
import kotlinx.android.synthetic.main.problem_photo_gallery.*
import kotlinx.android.synthetic.main.server_not_responding.*
import lt.vilnius.tvarkau.*
import lt.vilnius.tvarkau.backend.ApiMethod
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetProblemParams
import lt.vilnius.tvarkau.decorators.TextViewDecorator
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.extensions.applyReportStatusLabel
import lt.vilnius.tvarkau.utils.FormatUtils
import lt.vilnius.tvarkau.utils.GlobalConsts
import lt.vilnius.tvarkau.utils.NetworkUtils
import lt.vilnius.tvarkau.utils.PermissionUtils
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter
import org.parceler.Parcels
import rx.Subscription
import timber.log.Timber

/**
 * A fragment representing a single Problem detail screen.
 * This fragment is either contained in a [ProblemsListActivity]
 * in two-pane mode (on tablets) or a [ProblemDetailActivity]
 * on handsets.
 */
class ProblemDetailFragment : BaseFragment(), ProblemImagesPagerAdapter.ProblemImageClickedListener {

    private val issueId: String
        get() = arguments.getString(ARG_ITEM_ID)

    private lateinit var problem: Problem

    private var subscription: Subscription? = null

    private val clipboard by lazy {
        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.problem_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        problem_detail_view.visible()
        problem_answer_block.gone()
        no_internet_view.gone()
        server_not_responding_view.gone()

        problem_address.setOnClickListener {
            if (PermissionUtils.isAllPermissionsGranted(activity, MainActivity.MAP_PERMISSIONS)) {
                startProblemActivity(problem)
            } else {
                requestPermissions(MainActivity.MAP_PERMISSIONS, MainActivity.MAP_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getData()
    }

    private fun getData() {
        if (NetworkUtils.isNetworkConnected(activity)) {
            val params = GetProblemParams(issueId)
            val request = ApiRequest(ApiMethod.GET_REPORT, params)

            subscription = legacyApiService.getProblem(request)
                    .map { it.result!! }
                    .doOnNext { problem = it }
                    .doOnNext { analytics.trackViewProblem(it) }
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .subscribe({ problem ->
                        problem_detail_view.visible()
                        no_internet_view.gone()
                        server_not_responding_view.gone()

                        initProblemImagesPager(problem)

                        problem.id?.let { problem_id.text = it }
                        problem.getType()?.let { problem_title.text = it }
                        problem.description?.let { addProblemSpans(problem_description, it) }
                        problem.address?.let { problem_address.text = it }
                        problem.getEntryDate()?.let {
                            problem_entry_date.text = FormatUtils.formatLocalDateTime(it)
                        }
                        problem.status?.let {
                            problem.applyReportStatusLabel(problem_status!!)
                        }
                        problem.answer?.let {
                            problem_answer_block.visible()
                            addProblemSpans(problem_answer, it)
                            problem_answer_date.text = problem.completeDate
                        }

                        problem_images_view_pager_indicator.goneIf(problem.photos.orEmpty().isEmpty())
                    }, {
                        Timber.e(it)
                        no_internet_view.gone()
                        problem_detail_view.gone()
                        server_not_responding_view.visible()
                        showNoConnectionSnackbar()

                        Toast.makeText(context, R.string.error_no_problem, Toast.LENGTH_SHORT).show()
                    }).apply { subscription = this }
        } else {
            problem_detail_view.gone()
            server_not_responding_view.gone()
            no_internet_view.visible()
            showNoConnectionSnackbar()
        }
    }

    private fun addProblemSpans(textView: TextView, text: String) {
        TextViewDecorator(textView).decorateProblemIdSpans(text)
    }

    private fun showNoConnectionSnackbar() {
        if (activity != null) {
            Snackbar.make(activity.findViewById(R.id.problem_detail_coordinator_layout), R.string.no_connection, Snackbar
                    .LENGTH_INDEFINITE)
                    .setActionTextColor(ContextCompat.getColor(context, R.color.snackbar_action_text))
                    .setAction(R.string.try_again) { v -> getData() }
                    .show()
        } else {
            context?.let { Toast.makeText(it, R.string.no_connection, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun initProblemImagesPager(problem: Problem) {
        problem_images_view_pager.adapter = ProblemImagesPagerAdapter(problem, this)
        problem_images_view_pager.offscreenPageLimit = 3

        problem_images_view_pager_indicator.setViewPager(problem_images_view_pager)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MainActivity.MAP_PERMISSION_REQUEST_CODE
                && PermissionUtils.isAllPermissionsGranted(activity, MainActivity.MAP_PERMISSIONS)) {
            startProblemActivity(problem)
        } else {
            Toast.makeText(activity, R.string.error_need_location_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startProblemActivity(problem: Problem) {
        val intent = Intent(activity, ProblemsMapActivity::class.java)

        val data = Bundle()
        data.putString(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT)
        data.putParcelable(KEY_PROBLEM, Parcels.wrap<Problem>(problem))

        intent.putExtras(data)

        startActivity(intent)
    }

    override fun onMapClicked() {
        startProblemActivity(problem)
    }

    override fun onPhotoClicked(position: Int, photos: List<String>) {
        val intent = Intent(context, FullscreenImageActivity::class.java)
        intent.putExtra(FullscreenImageActivity.EXTRA_PHOTOS, photos.toTypedArray())
        intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_POSITION, position)

        context.startActivity(intent)
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()
        super.onDestroyView()
    }

    companion object {

        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        const val KEY_PROBLEM = "problem"

        fun getInstance(problemId: String): ProblemDetailFragment {
            return ProblemDetailFragment().apply {
                arguments = Bundle()
                arguments.putString(ARG_ITEM_ID, problemId)
            }
        }
    }

}
