package lt.vilnius.tvarkau.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.vinted.extensions.gone
import com.vinted.extensions.visible
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_new_report.*
import kotlinx.android.synthetic.main.image_picker_dialog.view.*
import kotlinx.android.synthetic.main.problem_detail.*
import lt.vilnius.tvarkau.FullscreenImageActivity
import lt.vilnius.tvarkau.NewProblemActivity.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent
import lt.vilnius.tvarkau.mvp.interactors.NewReportInteractorImpl
import lt.vilnius.tvarkau.mvp.interactors.ReportPhotoProviderImpl
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import lt.vilnius.tvarkau.mvp.presenters.NewReportPresenter
import lt.vilnius.tvarkau.mvp.presenters.NewReportPresenterImpl
import lt.vilnius.tvarkau.mvp.views.NewReportView
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import lt.vilnius.tvarkau.utils.KeyboardUtils
import lt.vilnius.tvarkau.utils.PermissionUtils
import lt.vilnius.tvarkau.views.adapters.NewProblemPhotosPagerAdapter
import org.greenrobot.eventbus.EventBus
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

/**
 * @author Martynas Jurkus
 */
class NewReportFragment : BaseFragment(),
        NewProblemPhotosPagerAdapter.OnPhotoClickedListener,
        NewReportView {

    var locationCords: LatLng? = null
    var imageFiles = ArrayList<File>()

    val presenter: NewReportPresenter by lazy {
        NewReportPresenterImpl(
                NewReportInteractorImpl(
                        legacyApiService,
                        ReportPhotoProviderImpl(context),
                        ioScheduler
                ),
                this,
                uiScheduler
        )
    }

    var progressDialog: ProgressDialog? = null

    val validatePersonalData: Boolean
        get() = reportType == TRAFFIC_VIOLATIONS

    lateinit var reportType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            locationCords = it.getParcelable(SAVE_LOCATION)
            imageFiles = it.getSerializable(SAVE_PHOTOS) as ArrayList<File>

        }

        reportType = arguments.getString(KEY_REPORT_TYPE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_report, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(activity as AppCompatActivity) {
            setSupportActionBar(toolbar)

            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeAsUpIndicator(R.drawable.ic_close)
                it.title = reportType
            }
        }

        problem_images_view_pager.adapter = NewProblemPhotosPagerAdapter(imageFiles, this)
        problem_images_view_pager.offscreenPageLimit = 3
        problem_images_view_pager_indicator.setViewPager(problem_images_view_pager)
        problem_images_view_pager_indicator.gone()

        report_problem_location.setOnClickListener { view ->
            onProblemLocationClicked(view)
        }

        report_problem_take_photo.setOnClickListener { onTakePhotoClicked() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_problem_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_send -> {
                presenter.submitProblem(createValidator())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttach()
        presenter.initWithReportType(reportType)
    }

    override fun showPersonalDataFields(profile: Profile?) {
        new_report_date_time_container.visible()
        new_report_birthday_container.visible()
        new_report_email_container.visible()
        new_report_name_container.visible()

        //TODO fill personal data fields
    }

    private fun createValidator(): FieldAwareValidator<NewReportData> {
        val data = NewReportData(
                description = report_problem_description.text.toString(),
                reportType = reportType,
                address = problem_address.text.toString(),
                latitude = locationCords?.latitude,
                longitude = locationCords?.longitude,
                email = null,
                phone = null,
                name = null,
                dateOfBirth = null,
                photoUrls = imageFiles
        )

        val validator = FieldAwareValidator.of(data)
                .validate({ it.address.isNotBlank() },
                        report_problem_location_wrapper.id,
                        getText(R.string.error_problem_location_is_empty).toString())
                .validate({ report_problem_description.text.isNotBlank() },
                        report_problem_description_wrapper.id,
                        getText(R.string.error_problem_description_is_empty).toString())

        return if (validatePersonalData) {
            //TODO validate fields
            validator.validate({ false }, 1, "")
        } else {
            validator
        }
    }

    override fun showValidationError(error: FieldAwareValidator.ValidationException) {
        report_problem_location_wrapper.error = null
        report_problem_description_wrapper.error = null

        view?.findViewById(error.viewId)?.let {
            if (it is TextInputLayout) {
                it.error = error.message
            }
        }
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context, R.string.error_submitting_problem, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(reportId: String) {
        //TODO move to interactor when shared pref wrapper is implemented
        myProblemsPreferences
                .edit()
                .putString(PROBLEM_PREFERENCE_KEY + reportId, reportId)
                .apply()

        EventBus.getDefault().post(NewProblemAddedEvent())

        activity.currentFocus?.run {
            KeyboardUtils.closeSoftKeyboard(activity, this)
        }

        Toast.makeText(context, R.string.problem_successfully_sent, Toast.LENGTH_SHORT).show()
        //TODO pass through to activity to complete navigation
    }

    fun onTakePhotoClicked() {
        if (PermissionUtils.isAllPermissionsGranted(activity, TAKE_PHOTO_PERMISSIONS)) {
            openPhotoSelectorDialog()
        } else {
            requestPermissions(activity, TAKE_PHOTO_PERMISSIONS, TAKE_PHOTO_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun openPhotoSelectorDialog() {
        val imagePickerDialogBuilder = AlertDialog.Builder(context, R.style.MyDialogTheme)

        val view = LayoutInflater.from(context).inflate(R.layout.image_picker_dialog, null)
        val cameraButton = view.camera_button
        val galleryButton = view.gallery_button

        if (!EasyImage.canDeviceHandleGallery(context)) {
            galleryButton.gone()
        }

        imagePickerDialogBuilder
                .setTitle(R.string.add_photos)
                .setView(view)
                .setPositiveButton(R.string.cancel) { dialog, whichButton ->
                    dialog.dismiss()
                }
                .create()

        val imagePickerDialog = imagePickerDialogBuilder.show()

        cameraButton.setOnClickListener {
            EasyImage.openCamera(this, 0)
            imagePickerDialog.dismiss()
        }

        galleryButton.setOnClickListener {
            EasyImage.openGallery(this, 0, true)
            imagePickerDialog.dismiss()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            TAKE_PHOTO_PERMISSIONS_REQUEST_CODE -> if (PermissionUtils.isAllPermissionsGranted(activity, TAKE_PHOTO_PERMISSIONS)) {
                openPhotoSelectorDialog()
            } else {
                Toast.makeText(context, R.string.error_need_camera_and_storage_permission, Toast.LENGTH_SHORT).show()
            }
            MAP_PERMISSION_REQUEST_CODE -> if (PermissionUtils.isAllPermissionsGranted(activity, MAP_PERMISSIONS)) {
                showPlacePicker(activity.currentFocus)
            } else {
                Toast.makeText(context, R.string.error_need_location_permission, Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun isEditedByUser(): Boolean {
        return report_problem_description.text.isNotBlank()
                || report_problem_location.text.isNotBlank()
                || imageFiles.isNotEmpty()
    }

    //TODO handle on back pressed
    fun onBackPressed() {
        if (report_problem_description.hasFocus()) {
            KeyboardUtils.closeSoftKeyboard(activity, report_problem_description)
        }
        if (isEditedByUser()) {
            AlertDialog.Builder(context, R.style.MyDialogTheme)
                    .setMessage(getString(R.string.discard_changes_title))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.discard_changes_positive) {
                        dialog, whichButton ->
                        activity.onBackPressed()
                    }
                    .setNegativeButton(R.string.discard_changes_negative, null).show()
        } else {
            activity.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, activity, object : DefaultCallback() {
            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                Toast.makeText(activity, R.string.photo_capture_error, Toast.LENGTH_SHORT).show()
                Timber.w(e, "Unable to take a picture")
            }

            override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                setImagesInViewPager(imageFiles)
            }

            override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(context)
                    photoFile?.delete()
                }
            }
        })

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PLACE_PICKER -> {
                    val geocoder = Geocoder(context)
                    val place = PlacePicker.getPlace(context, data)

                    var addresses = listOf<Address>()

                    place.latLng?.let {
                        locationCords = it

                        try {
                            addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        } catch (e: IOException) {
                            Timber.e(e)
                        }
                    }

                    if (addresses.isNotEmpty()) {
                        if (addresses.first().locality != null) {
                            val address = addresses[0].getAddressLine(0)
                            report_problem_location_wrapper.error = null
                            report_problem_location.setText(address)
                        }
                    } else {
                        // Mostly when Geocoder throws IOException
                        // backup solution which in not 100% reliable
                        val addressSlice = place.address.toString()
                                .split(", ".toRegex())
                                .dropLastWhile(String::isEmpty)
                                .toTypedArray()

                        val address = addressSlice[0]
                        report_problem_location_wrapper.error = null
                        report_problem_location.setText(address)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            it.putParcelable(SAVE_LOCATION, locationCords)
            it.putSerializable(SAVE_PHOTOS, imageFiles)
        }
    }

    private fun setImagesInViewPager(newImageFiles: List<File>) {
        this.imageFiles.addAll(newImageFiles)

        problem_images_view_pager.adapter.notifyDataSetChanged()

        if (imageFiles.size > 1) {
            problem_images_view_pager_indicator.visible()
            problem_images_view_pager.currentItem = imageFiles.size - 1
        }
    }

    fun onProblemLocationClicked(view: View) {
        if (PermissionUtils.isAllPermissionsGranted(activity, MAP_PERMISSIONS)) {
            showPlacePicker(view)
        } else {
            requestPermissions(activity, MAP_PERMISSIONS, MAP_PERMISSION_REQUEST_CODE)
        }
    }

    private fun showPlacePicker(view: View) {
        val builder = PlacePicker.IntentBuilder()
        try {
            val intent = builder.build(activity)
            val bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.width, view.height).toBundle()
            startActivityForResult(intent, REQUEST_PLACE_PICKER, bundle)
        } catch (e: GooglePlayServicesRepairableException) {
            Timber.e(e)
            Snackbar.make(view, R.string.check_google_play_services, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.open) {
                        val intent = Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"))
                        startActivity(intent)
                    }
                    .setActionTextColor(getColor(context, R.color.snackbar_action_text))
                    .show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            Timber.e(e)
            Snackbar.make(view, R.string.check_google_play_services, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.open) {
                        val intent = Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"))
                        startActivity(intent)
                    }.setActionTextColor(getColor(context, R.color.snackbar_action_text)).show()
        }
    }

    override fun onPhotoClicked(position: Int, photos: List<String>) {
        val intent = Intent(activity, FullscreenImageActivity::class.java)
        intent.putExtra(FullscreenImageActivity.EXTRA_PHOTOS, photos.toTypedArray())
        intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_POSITION, position)

        startActivity(intent)
    }

    override fun showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context).apply {
                setMessage(getString(R.string.sending_problem))
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                setCancelable(false)
            }
        } else {
            progressDialog?.show()
        }
    }

    override fun hideProgress() {
        progressDialog?.hide()
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
    }

    companion object {
        const val TRAFFIC_VIOLATIONS = "Transporto priemonių stovėjimo tvarkos pažeidimai"

        private const val SAVE_LOCATION = "location"
        private const val SAVE_PHOTOS = "photos"

        const val KEY_REPORT_TYPE = "report_type"

        fun newInstance(problemType: String): NewReportFragment {
            return NewReportFragment().apply {
                arguments = Bundle()
                arguments.putString(KEY_REPORT_TYPE, problemType)
            }
        }
    }
}