package lt.vilnius.tvarkau.fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.content.res.AppCompatResources
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_new_report.*
import kotlinx.android.synthetic.main.image_picker_dialog.view.*
import lt.vilnius.tvarkau.FullscreenImageActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.activity.available
import lt.vilnius.tvarkau.activity.googlePlayServicesAvailability
import lt.vilnius.tvarkau.activity.resolutionDialog
import lt.vilnius.tvarkau.activity.resultCode
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.SharedPreferencesMyReportsInteractor
import lt.vilnius.tvarkau.mvp.interactors.NewReportInteractorImpl
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractorImpl
import lt.vilnius.tvarkau.mvp.interactors.ReportPhotoProviderImpl
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import lt.vilnius.tvarkau.mvp.presenters.NewReportPresenter
import lt.vilnius.tvarkau.mvp.presenters.NewReportPresenterImpl
import lt.vilnius.tvarkau.mvp.views.NewReportView
import lt.vilnius.tvarkau.rx.RxBus
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import lt.vilnius.tvarkau.utils.FormatUtils.formatLocalDateTime
import lt.vilnius.tvarkau.utils.KeyboardUtils
import lt.vilnius.tvarkau.utils.PermissionUtils
import lt.vilnius.tvarkau.utils.PersonalCodeValidator
import lt.vilnius.tvarkau.utils.SharedPrefsManager
import lt.vilnius.tvarkau.views.adapters.NewProblemPhotosPagerAdapter
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

/**
 * @author Martynas Jurkus
 */
@Screen(navigationMode = NavigationMode.BACK,
        trackingScreenName = ActivityConstants.SCREEN_NEW_REPORT)
class NewReportFragment : BaseFragment(),
        NewProblemPhotosPagerAdapter.OnPhotoClickedListener,
        NewReportView {

    private var googlePlayServicesResolutionDialog: Dialog? = null

    var locationCords: LatLng? = null
    var imageFiles = ArrayList<File>()

    private val presenter: NewReportPresenter by lazy {
        NewReportPresenterImpl(
                NewReportInteractorImpl(
                        legacyApiService,
                        SharedPreferencesMyReportsInteractor(myProblemsPreferences),
                        ReportPhotoProviderImpl(context!!),
                        ioScheduler,
                        getString(R.string.report_description_timestamp_template),
                        analytics
                ),
                PersonalDataInteractorImpl(
                        SharedPrefsManager.getInstance(context!!)
                ),
                this,
                uiScheduler,
                analytics
        )
    }

    private var progressDialog: ProgressDialog? = null

    private val validateParkingViolationData: Boolean
        get() = reportType == PARKING_VIOLATIONS

    private val shouldDisplayPhotoInstructions: Boolean
        get() {
            val delta = System.currentTimeMillis() - appPreferences.photoInstructionsLastSeen.get()
            return reportType == PARKING_VIOLATIONS && Duration.ofMillis(delta).toDays() >= 1
        }

    private lateinit var reportType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            locationCords = it.getParcelable(SAVE_LOCATION)
            imageFiles = it.getSerializable(SAVE_PHOTOS) as ArrayList<File>

        }

        reportType = arguments!!.getString(KEY_REPORT_TYPE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        with(activity!! as AppCompatActivity) {
            setSupportActionBar(toolbar)

            supportActionBar?.title = reportType
        }

        problem_images_view_pager.adapter = NewProblemPhotosPagerAdapter(imageFiles, this)
        problem_images_view_pager.offscreenPageLimit = 3
        problem_images_view_pager_indicator.setViewPager(problem_images_view_pager)
        problem_images_view_pager_indicator.gone()

        report_problem_location.setOnClickListener { view ->
            onProblemLocationClicked(view)
        }

        report_problem_take_photo.setOnClickListener { onTakePhotoClicked() }

        report_problem_date_time.setOnClickListener {
            onProblemDateTimeClicked()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_problem_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onGoBack()
                return true
            }
            R.id.action_send -> {
                activity!!.currentFocus?.let {
                    KeyboardUtils.closeSoftKeyboard(activity!!, it)
                }

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

        EasyImage.configuration(context!!).setAllowMultiplePickInGallery(true)
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun showParkingViolationFields(profile: Profile?) {
        new_report_date_time_container.visible()
        new_report_licence_plate_container.visible()

        val drawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_autorenew)
        report_problem_date_time.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        report_problem_date_time.setOnTouchListener { view, event ->
            val DRAWABLE_RIGHT = 2

            var result = false
            val ediText = view as EditText
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (ediText.right - ediText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    report_problem_date_time.setText(formatLocalDateTime(LocalDateTime.now()))
                    result = true
                }
            }

            result
        }

        new_report_personal_code_container.visible()
        new_report_email_container.visible()
        new_report_name_container.visible()
        report_problem_personal_data_agreement.visible()

        profile?.let {
            report_problem_submitter_email.setText(it.email)
            report_problem_submitter_name.setText(it.name)
            report_problem_submitter_personal_code.setText(it.personalCode)
        }
    }

    private fun createValidator(): FieldAwareValidator<NewReportData> {
        val data = NewReportData(
                reportType = reportType,
                description = report_problem_description.text.toString(),
                address = report_problem_location.text.toString(),
                latitude = locationCords?.latitude,
                longitude = locationCords?.longitude,
                dateTime = report_problem_date_time.text.toString(),
                email = report_problem_submitter_email.text.toString(),
                name = report_problem_submitter_name.text.toString(),
                personalCode = report_problem_submitter_personal_code.text.toString(),
                photoUrls = imageFiles,
                licencePlate = report_problem_licence_plate_number.text.toString()
        )

        var validator = FieldAwareValidator.of(data)
                .validate({ it.address.isNotBlank() },
                        report_problem_location_wrapper.id,
                        getText(R.string.error_problem_location_is_empty).toString())
                .validate({ report_problem_description.text.isNotBlank() },
                        report_problem_description_wrapper.id,
                        getText(R.string.error_problem_description_is_empty).toString())

        if (validateParkingViolationData) {
            validator = validator
                    .validate({ it.licencePlate?.isNotBlank() ?: false },
                            report_problem_licence_plate_number_wrapper.id,
                            getString(R.string.error_new_report_fill_licence_plate))
                    .validate({ it.dateTime?.isNotBlank() ?: false },
                            report_problem_date_time_wrapper.id,
                            getString(R.string.error_report_fill_date_time))
                    .validate({ it.email?.isNotBlank() ?: false },
                            report_problem_submitter_email_wrapper.id,
                            getString(R.string.error_profile_fill_email))
                    .validate({ Patterns.EMAIL_ADDRESS.matcher(it.email).matches() },
                            report_problem_submitter_email_wrapper.id,
                            getString(R.string.error_profile_email_invalid))
                    .validate({ it.name?.isNotBlank() ?: false },
                            report_problem_submitter_name_wrapper.id,
                            getText(R.string.error_profile_fill_name).toString())
                    .validate({ it.name!!.split(" ").size >= 2 },
                            report_problem_submitter_name_wrapper.id,
                            getText(R.string.error_profile_name_invalid).toString())
                    .validate({ it.personalCode?.isNotBlank() ?: false },
                            report_problem_submitter_personal_code_wrapper.id,
                            getText(R.string.error_new_report_enter_personal_code).toString())
                    .validate({ PersonalCodeValidator.validate(it.personalCode!!) },
                            report_problem_submitter_personal_code_wrapper.id,
                            getText(R.string.error_new_report_invalid_personal_code).toString())
                    .validate({ it.photoUrls.size >= 2 },
                            0,
                            getText(R.string.error_minimum_photo_requirement).toString())
        }

        return validator
    }

    override fun showValidationError(error: FieldAwareValidator.ValidationException) {
        report_problem_location_wrapper.isErrorEnabled = false
        report_problem_description_wrapper.isErrorEnabled = false
        report_problem_date_time_wrapper.isErrorEnabled = false
        report_problem_submitter_name_wrapper.isErrorEnabled = false
        report_problem_submitter_personal_code_wrapper.isErrorEnabled = false
        report_problem_submitter_email_wrapper.isErrorEnabled = false
        report_problem_licence_plate_number_wrapper.isErrorEnabled = false

        view?.findViewById<TextInputLayout>(error.viewId)?.let {
            it.error = error.message
        } ?: Toast.makeText(context!!, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(error: Throwable) {
        Toast.makeText(context!!, R.string.error_submitting_problem, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess() {
        RxBus.publish(NewProblemAddedEvent())

        activity!!.currentFocus?.run {
            KeyboardUtils.closeSoftKeyboard(activity!!, this)
        }

        Toast.makeText(context!!, R.string.problem_successfully_sent, Toast.LENGTH_SHORT).show()
        (activity!! as ReportRegistrationActivity).onReportSubmitted()
    }

    override fun fillReportDateTime(dateTime: String) {
        report_problem_date_time.setText(dateTime)
    }

    override fun displayImages(imageFiles: List<File>) {
        this.imageFiles.addAll(imageFiles)

        problem_images_view_pager.adapter!!.notifyDataSetChanged()

        if (this.imageFiles.size > 1) {
            problem_images_view_pager_indicator.visible()
            problem_images_view_pager.currentItem = this.imageFiles.lastIndex
        }
    }

    fun onTakePhotoClicked() {
        if (PermissionUtils.isAllPermissionsGranted(activity!!, TAKE_PHOTO_PERMISSIONS)) {
            openPhotoSelectorDialog(shouldDisplayPhotoInstructions)
        } else {
            requestPermissions(TAKE_PHOTO_PERMISSIONS, ActivityConstants.REQUEST_CODE_TAKE_PHOTO_PERMISSIONS)
        }
    }

    private fun openPhotoSelectorDialog(displayPhotoInstructions: Boolean) {
        activity!!.currentFocus?.let { KeyboardUtils.closeSoftKeyboard(activity!!, it) }

        if (displayPhotoInstructions) {
            arguments!!.putBoolean(KEY_TAKE_PHOTO, true)
            (activity!! as ReportRegistrationActivity).displayPhotoInstructions()
            return
        }

        val imagePickerDialogBuilder = AlertDialog.Builder(context!!, R.style.MyDialogTheme)

        val view = LayoutInflater.from(context!!).inflate(R.layout.image_picker_dialog, null)
        val cameraButton = view.camera_button
        val galleryButton = view.gallery_button

        if (!EasyImage.canDeviceHandleGallery(context!!)) {
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
            EasyImage.openGallery(this, 0)
            imagePickerDialog.dismiss()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ActivityConstants.REQUEST_CODE_TAKE_PHOTO_PERMISSIONS -> if (PermissionUtils.isAllPermissionsGranted(activity!!, TAKE_PHOTO_PERMISSIONS)) {
                openPhotoSelectorDialog(shouldDisplayPhotoInstructions)
            } else {
                Toast.makeText(context!!, R.string.error_need_camera_and_storage_permission, Toast.LENGTH_SHORT).show()
            }
            ActivityConstants.REQUEST_CODE_MAP_PERMISSION -> if (PermissionUtils.isAllPermissionsGranted(activity!!, MAP_PERMISSIONS)) {
                showPlacePicker(view!!)
            } else {
                Toast.makeText(context!!, R.string.error_need_location_permission, Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun isEditedByUser(): Boolean {
        return report_problem_description.text.isNotBlank()
                || report_problem_location.text.isNotBlank()
                || imageFiles.isNotEmpty()
    }

    private fun onGoBack() {
        if (report_problem_description.hasFocus()) {
            KeyboardUtils.closeSoftKeyboard(activity!!, report_problem_description)
        }
        if (isEditedByUser()) {
            AlertDialog.Builder(context!!, R.style.MyDialogTheme)
                    .setMessage(getString(R.string.discard_changes_title))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.discard_changes_positive) { dialog, whichButton ->
                        activity!!.onBackPressed()
                    }
                    .setNegativeButton(R.string.discard_changes_negative, null).show()
        } else {
            activity!!.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, activity!!, object : DefaultCallback() {
            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                Toast.makeText(activity!!, R.string.photo_capture_error, Toast.LENGTH_SHORT).show()
                Timber.w(e, "Unable to take a picture")
            }

            override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                presenter.onImagesPicked(imageFiles)
            }

            override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(context!!)
                    photoFile?.delete()
                }
            }
        })

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ActivityConstants.REQUEST_CODE_PLACE_PICKER -> {
                    val geocoder = Geocoder(context!!)
                    val place = PlacePicker.getPlace(context!!, data)

                    val addresses = mutableListOf<Address>()

                    place.latLng?.let {
                        locationCords = it

                        try {
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)?.let {
                                addresses.addAll(it)
                            }
                        } catch (e: Throwable) {
                            Timber.e(GeocoderException(e))
                        }
                    }

                    val firstAddress = addresses.firstOrNull()
                    if (firstAddress?.locality != null) {
                        val address = firstAddress.getAddressLine(0)
                        report_problem_location_wrapper.error = null
                        report_problem_location.setText(address)
                    } else {
                        // Mostly when Geocoder throws IOException
                        // backup solution which in not 100% reliable
                        val addressSlice = place.address
                                ?.split(", ".toRegex())
                                ?.dropLastWhile(String::isEmpty)
                                ?.toTypedArray()

                        val addressText = if (addressSlice == null || addressSlice.isEmpty()) {
                            locationCords?.let {
                                "${it.latitude}; ${it.longitude}"
                            }
                        } else {
                            addressSlice[0]
                        }

                        if (addressText.isNullOrEmpty()) {
                            report_problem_location_wrapper.error = getText(R.string.error_failed_to_determine_address)
                            report_problem_location.text = null
                        } else {
                            report_problem_location_wrapper.error = null
                            report_problem_location.setText(addressText)
                        }
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVE_LOCATION, locationCords)
        outState.putSerializable(SAVE_PHOTOS, imageFiles)
    }

    private fun onProblemLocationClicked(view: View) {
        if (PermissionUtils.isAllPermissionsGranted(activity!!, MAP_PERMISSIONS)) {
            showPlacePicker(view)
        } else {
            requestPermissions(MAP_PERMISSIONS, ActivityConstants.REQUEST_CODE_MAP_PERMISSION)
        }
    }


    private fun showPlacePicker(view: View) {
        val googlePlayServicesAvailability = activity!!.googlePlayServicesAvailability()

        if (googlePlayServicesAvailability.available()) {
            val intent = PlacePicker.IntentBuilder().build(activity!!)
            val bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.width, view.height).toBundle()
            startActivityForResult(intent, ActivityConstants.REQUEST_CODE_PLACE_PICKER, bundle)
        } else {
            analytics.trackGooglePlayServicesError(googlePlayServicesAvailability.resultCode())

            googlePlayServicesResolutionDialog?.dismiss()
            googlePlayServicesResolutionDialog = googlePlayServicesAvailability.resolutionDialog(activity!!)
            googlePlayServicesResolutionDialog?.show()
        }
    }

    private fun onProblemDateTimeClicked() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val year = calendar.get(YEAR)
        val month = calendar.get(MONTH)
        val day = calendar.get(DAY_OF_MONTH)

        val dialogDatePicker = DatePickerDialog(
                activity!!,
                { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                    calendar.set(YEAR, year)
                    calendar.set(MONTH, month)
                    calendar.set(DAY_OF_MONTH, day)

                    TimePickerDialog(activity!!, { timePicker: TimePicker, hour: Int, minutes: Int ->
                        calendar.set(HOUR_OF_DAY, hour)
                        calendar.set(MINUTE, minutes)

                        val dateTime = LocalDateTime.of(
                                calendar.get(YEAR),
                                calendar.get(MONTH) + 1, //LocalDateTime expects month starting from 1 instead of 0
                                calendar.get(DAY_OF_MONTH),
                                calendar.get(HOUR_OF_DAY),
                                calendar.get(MINUTE)
                        )

                        report_problem_date_time.setText(formatLocalDateTime(dateTime))
                    }, 0, 0, true).show()
                }, year, month, day)

        dialogDatePicker.datePicker.maxDate = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
        dialogDatePicker.setTitle(null)
        dialogDatePicker.show()
    }

    override fun onPhotoClicked(position: Int, photos: List<String>) {
        val intent = Intent(activity!!, FullscreenImageActivity::class.java)
        intent.putExtra(FullscreenImageActivity.EXTRA_PHOTOS, photos.toTypedArray())
        intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_POSITION, position)

        startActivity(intent)
    }

    override fun showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context!!).apply {
                setMessage(getString(R.string.sending_problem))
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                setCancelable(false)
            }
        }

        progressDialog?.show()
    }

    override fun hideProgress() {
        progressDialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        if (arguments!!.containsKey(KEY_TAKE_PHOTO)) {
            arguments!!.remove(KEY_TAKE_PHOTO)
            openPhotoSelectorDialog(displayPhotoInstructions = false)
        }
    }

    override fun onDestroy() {
        EasyImage.clearConfiguration(context!!)
        super.onDestroy()
    }

    override fun onDestroyView() {
        progressDialog?.dismiss()
        presenter.onDetach()
        super.onDestroyView()
    }

    companion object {

        val TAKE_PHOTO_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        val MAP_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        const val PARKING_VIOLATIONS = "Transporto priemonių stovėjimo tvarkos pažeidimai"

        private const val SAVE_LOCATION = "location"
        private const val SAVE_PHOTOS = "photos"

        const val KEY_REPORT_TYPE = "report_type"
        const val KEY_TAKE_PHOTO = "take_photo"


        fun newInstance(reportType: ReportType): NewReportFragment {
            return NewReportFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_REPORT_TYPE, reportType)
                }
            }
        }
    }

    class GeocoderException(cause: Throwable) : RuntimeException(cause)
}
