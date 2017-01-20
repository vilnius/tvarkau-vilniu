package lt.vilnius.tvarkau.fragments

import kotlinx.android.synthetic.main.fragment_new_report.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.assertj.TextInputLayoutAssert.Companion.assertThat
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.base.BaseRobolectricTest
import org.assertj.android.api.Assertions.assertThat
import org.junit.Test
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowToast
import java.io.File
import javax.inject.Inject
import kotlin.test.assertEquals

/**
 * @author Martynas Jurkus
 */
class NewReportFragmentTest : BaseRobolectricTest() {

    @Inject
    lateinit var api: LegacyApiService

    override fun setUp() {
        super.setUp()
        activity.getTestComponent().inject(this)
    }

    @Test
    fun init_anyReportType_correctLayout() {
        val fragment = setUpFragment("any")

        assertThat(fragment.report_problem_description_wrapper).isVisible
        assertThat(fragment.report_problem_location_wrapper).isVisible
        assertThat(fragment.new_report_email_container).isNotVisible
        assertThat(fragment.new_report_name_container).isNotVisible
        assertThat(fragment.new_report_date_time_container).isNotVisible
        assertThat(fragment.new_report_personal_code_container).isNotVisible
    }

    @Test
    fun init_parkingViolations_correctLayout() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        assertThat(fragment.report_problem_location_wrapper).isVisible
        assertThat(fragment.report_problem_description_wrapper).isVisible
        assertThat(fragment.new_report_email_container).isVisible
        assertThat(fragment.new_report_name_container).isVisible
        assertThat(fragment.new_report_date_time_container).isVisible
        assertThat(fragment.new_report_personal_code_container).isVisible
    }

    @Test
    fun submit_simpleReport_locationValidationFails() {
        val fragment = setUpFragment("any")

        fragment.doSubmitReport()

        assertThat(fragment.report_problem_location_wrapper).hasError(R.string.error_problem_location_is_empty)
    }

    @Test
    fun submit_simpleReport_descriptionValidationFails() {
        val fragment = setUpFragment("any")
        fragment.report_problem_location.setText("Some location")

        fragment.doSubmitReport()

        assertThat(fragment.report_problem_description_wrapper).hasError(R.string.error_problem_description_is_empty)
    }

    @Test
    fun submit_simpleReport_validationReset() {
        val fragment = setUpFragment("any")

        fragment.doSubmitReport()
        assertThat(fragment.report_problem_location_wrapper).hasError(R.string.error_problem_location_is_empty)

        //update form
        fragment.report_problem_location.setText("Some location")

        fragment.doSubmitReport()

        assertThat(fragment.report_problem_location_wrapper).noError()
        assertThat(fragment.report_problem_description_wrapper)
                .hasError(R.string.error_problem_description_is_empty)
    }

    @Test
    fun submit_parkingViolation_validateName() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        fillAllFields(fragment)
        fragment.report_problem_submitter_name.setText("")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_name_wrapper)
                .hasError(R.string.error_profile_fill_name)

        fragment.report_problem_submitter_name.setText("myname")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_name_wrapper)
                .hasError(R.string.error_profile_name_invalid)

        fragment.report_problem_submitter_name.setText("valid name")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_name_wrapper).noError()
    }

    @Test
    fun submit_parkingViolation_validateEmail() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        fillAllFields(fragment)
        fragment.report_problem_submitter_email.setText("")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_email_wrapper)
                .hasError(R.string.error_profile_fill_email)

        fragment.report_problem_submitter_email.setText("invalidemail")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_email_wrapper)
                .hasError(R.string.error_profile_email_invalid)

        fragment.report_problem_submitter_email.setText("valid@email.com")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_email_wrapper).noError()
    }

    @Test
    fun submit_parkingViolation_validatePersonalCode() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        fillAllFields(fragment)
        fragment.report_problem_submitter_personal_code.setText("")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_personal_code_wrapper)
                .hasError(R.string.error_new_report_enter_personal_code)

        fragment.report_problem_submitter_personal_code.setText("zoom")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_personal_code_wrapper)
                .hasError(R.string.error_new_report_invalid_personal_code)

        fragment.report_problem_submitter_personal_code.setText("34508028198")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_submitter_personal_code_wrapper)
                .noError()
    }

    @Test
    fun submit_parkingViolation_validateDateTime() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        fillAllFields(fragment)
        fragment.report_problem_date_time.setText("")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_date_time_wrapper)
                .hasError(R.string.error_report_fill_date_time)

        fragment.report_problem_date_time.setText("2017-01-01 00:00")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_date_time_wrapper)
                .noError()
    }

    @Test
    fun submit_parkingViolation_validatePhotos() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        fillAllFields(fragment)
        fragment.imageFiles.add(File("some file"))

        fragment.doSubmitReport()

        ShadowLooper.idleMainLooper()
        assertEquals(activity.getString(R.string.error_minimum_photo_requirement),
                ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun submit_parkingViolation_validateLacencePlate() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        fillAllFields(fragment)
        fragment.report_problem_licence_plate_number.setText("")
        fragment.doSubmitReport()

        assertThat(fragment.report_problem_licence_plate_number_wrapper)
                .hasError(R.string.error_new_report_fill_licence_plate)

        fragment.report_problem_licence_plate_number.setText("AAA111")
        fragment.doSubmitReport()
        assertThat(fragment.report_problem_licence_plate_number_wrapper)
                .noError()
    }

    private fun fillAllFields(fragment: NewReportFragment) {
        fragment.report_problem_location.setText("Some location")
        fragment.report_problem_description.setText("Some description")
        fragment.report_problem_submitter_email.setText("some@email.com")
        fragment.report_problem_submitter_name.setText("some name")
        fragment.report_problem_date_time.setText("2017-01-01 00:00")
        fragment.report_problem_submitter_personal_code.setText("34508028198")
        fragment.report_problem_licence_plate_number.setText("AAA111")
    }

    private fun NewReportFragment.doSubmitReport() {
        val item = object : RoboMenuItem() {
            override fun getItemId(): Int {
                return R.id.action_send
            }
        }

        this.onOptionsItemSelected(item)
    }

    private fun setUpFragment(reportType: String): NewReportFragment {
        return NewReportFragment.newInstance(reportType).apply {
            setFragment(this)
        }
    }
}