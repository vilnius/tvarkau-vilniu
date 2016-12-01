package lt.vilnius.tvarkau.fragments

import android.content.ClipData
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
import timber.log.Timber

/**
 * A fragment representing a single Problem detail screen.
 * This fragment is either contained in a [ProblemsListActivity]
 * in two-pane mode (on tablets) or a [ProblemDetailActivity]
 * on handsets.
 */
class ProblemDetailFragment : BaseFragment() {

    private val issueId: String
        get() = arguments.getString(ARG_ITEM_ID)

    private lateinit var problem: Problem

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
        problem_id.setOnLongClickListener {
            copyTextToClipboard(it, R.string.problem_id_copied_to_clipboard)
        }
        problem_title.setOnLongClickListener {
            copyTextToClipboard(it, R.string.category_copied_to_clipboard)
        }
        problem_address.setOnLongClickListener {
            copyTextToClipboard(it, R.string.address_copied_to_clipboard)
        }
        problem_entry_date.setOnLongClickListener {
            copyTextToClipboard(it, R.string.date_copied_to_clipboard)
        }
        problem_answer_date.setOnLongClickListener {
            copyTextToClipboard(it, R.string.date_copied_to_clipboard)
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

            legacyApiService.getProblem(request)
                    .map { it.result!! }
                    .doOnNext { problem = it }
                    .doOnNext { analytics.trackViewProblem(it) }
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .subscribe({ problem ->
                        problem_detail_view.visible()
                        no_internet_view.gone()
                        server_not_responding_view.gone()

                        problem.id?.let { problem_id.text = it }
                        problem.getType()?.let { problem_title.text = it }
                        problem.description?.let { problem_description.text = it }
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

                        problem.photos?.let {
                            if (it.size == 1) {
                                problem_images_view_pager_indicator.gone()
                            }

                            initProblemImagesPager(problem)
                        } ?: problem_image_pager_layout.gone()
                    }, {
                        Timber.e(it)
                        no_internet_view.gone()
                        problem_detail_view.gone()
                        server_not_responding_view.visible()
                        showNoConnectionSnackbar()

                        Toast.makeText(context, R.string.error_no_problem, Toast.LENGTH_SHORT).show()
                    })
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
        val photos = problem.photos

        problem_images_view_pager.adapter = ProblemImagesPagerAdapter(context, photos)
        problem_images_view_pager.offscreenPageLimit = 3

        problem_images_view_pager_indicator.setViewPager(problem_images_view_pager)
    }

    private fun copyTextToClipboard(source: View, resId: Int): Boolean {
        if (source !is TextView) return false
        clipboard.primaryClip = ClipData.newPlainText("data", source.text)
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
        return true
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
