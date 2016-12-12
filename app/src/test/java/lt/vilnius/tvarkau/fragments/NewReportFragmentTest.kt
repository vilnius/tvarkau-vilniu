package lt.vilnius.tvarkau.fragments

import kotlinx.android.synthetic.main.fragment_new_report.*
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.base.BaseRobolectricTest
import org.assertj.android.api.Assertions.assertThat
import org.junit.Test
import javax.inject.Inject

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
        assertThat(fragment.new_report_birthday_container).isNotVisible
    }

    @Test
    fun init_parkingViolations_correctLayout() {
        val fragment = setUpFragment(NewReportFragment.PARKING_VIOLATIONS)

        assertThat(fragment.report_problem_description_wrapper).isVisible
        assertThat(fragment.report_problem_location_wrapper).isVisible
        assertThat(fragment.new_report_email_container).isVisible
        assertThat(fragment.new_report_name_container).isVisible
        assertThat(fragment.new_report_date_time_container).isVisible
        assertThat(fragment.new_report_birthday_container).isVisible
    }

    private fun setUpFragment(reportType: String): NewReportFragment {
        return NewReportFragment.newInstance(reportType).apply {
            setFragment(this)
        }
    }
}