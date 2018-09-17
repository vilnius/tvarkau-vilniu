package lt.vilnius.tvarkau.fragments

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.loading.*
import kotlinx.android.synthetic.main.no_internet.*
import kotlinx.android.synthetic.main.problem_detail.*
import kotlinx.android.synthetic.main.problem_photo_gallery.*
import kotlinx.android.synthetic.main.server_not_responding.*
import lt.vilnius.tvarkau.FullscreenImageActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.ReportMapActivity
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.decorators.TextViewDecorator
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.extensions.*
import lt.vilnius.tvarkau.utils.GlobalConsts
import lt.vilnius.tvarkau.utils.PermissionUtils
import lt.vilnius.tvarkau.viewmodel.ReportDetailsViewModel
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter
import javax.inject.Inject

@Screen(
    titleRes = R.string.problem_description_title,
    navigationMode = NavigationMode.BACK,
    trackingScreenName = ActivityConstants.SCREEN_REPORT_DETAILS
)
class ReportDetailsFragment : BaseFragment() {

    private val reportId: Int
        get() = arguments!!.getInt(ARG_REPORT_ID)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ReportDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.problem_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        problem_address.setOnClickListener {
            if (PermissionUtils.isAllPermissionsGranted(activity!!, MAP_PERMISSIONS)) {
                startProblemActivity(viewModel.report.value!!)
            } else {
                requestPermissions(MAP_PERMISSIONS, MAP_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        showLoading()

        viewModel = withViewModel(viewModelFactory) {
            observeNonNull(report, ::showReport)
            observe(errorEvents, ::showError)

            initWith(reportId = reportId)
        }
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    private fun showError(throwable: Throwable?) {
        if (throwable == null) return

        Toast.makeText(context, R.string.error_no_problem, Toast.LENGTH_SHORT).show()
    }

    private fun showReport(reportEntity: ReportEntity) {
        hideLoading()

        analytics.trackViewReport(reportEntity)

        no_internet_view.gone()
        server_not_responding_view.gone()

        initProblemImagesPager(reportEntity)

        with(reportEntity) {
            problem_id.text = refNo
            problem_title.text = reportType.title
            problem_address.text = "TODO: show address"
            problem_entry_date.text = registeredAt

            addProblemSpans(problem_description, description)
            applyReportStatusLabel(problem_status)

            answer?.let {
                problem_answer_block.visible()
                addProblemSpans(problem_answer, it)
                problem_answer_date.text = completedAt
            }

            problem_images_view_pager_indicator.goneIf(photos.isEmpty())
        }

        problem_detail_scroll_view.visible()
    }

    private fun showLoading() {
        server_not_responding_view.gone()
        no_internet_view.gone()
        problem_detail_scroll_view.gone()
        problem_answer_block.gone()

        loading_view.visible()
    }

    private fun hideLoading() {
        loading_view.gone()
    }

    private fun addProblemSpans(textView: TextView, text: String) {
        TextViewDecorator(textView).decorateProblemIdSpans(text)
    }

    private fun initProblemImagesPager(reportEntity: ReportEntity) {
        problem_images_view_pager.adapter = ProblemImagesPagerAdapter(
            reportEntity = reportEntity,
            onImageClicked = ::onPhotoClicked,
            onMapClicked = ::onMapClicked
        )
        problem_images_view_pager.offscreenPageLimit = 3

        problem_images_view_pager_indicator.setViewPager(problem_images_view_pager)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MAP_PERMISSION_REQUEST_CODE
            && PermissionUtils.isAllPermissionsGranted(activity!!, MAP_PERMISSIONS)
        ) {
            startProblemActivity(viewModel.report.value!!)
        } else {
            Toast.makeText(activity!!, R.string.error_need_location_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startProblemActivity(reportEntity: ReportEntity) {
        val intent = Intent(activity!!, ReportMapActivity::class.java)

        val data = Bundle()
        data.putString(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT)
        data.putParcelable(KEY_PROBLEM, reportEntity)

        intent.putExtras(data)

        startActivity(intent)
    }

    private fun onMapClicked() {
        startProblemActivity(viewModel.report.value!!)
    }

    private fun onPhotoClicked(position: Int, photos: List<String>) {
        val intent = Intent(context, FullscreenImageActivity::class.java)
        intent.putExtra(FullscreenImageActivity.EXTRA_PHOTOS, photos.toTypedArray())
        intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_POSITION, position)

        context!!.startActivity(intent)
    }

    companion object {

        const val MAP_PERMISSION_REQUEST_CODE = 11
        val MAP_PERMISSIONS = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_REPORT_ID = "report_id"
        const val KEY_PROBLEM = "problem"

        fun getInstance(reportId: Int): ReportDetailsFragment {
            return ReportDetailsFragment().apply {
                arguments = Bundle()
                arguments!!.putInt(ARG_REPORT_ID, reportId)
            }
        }
    }

}
