package lt.vilnius.tvarkau.fragments

import android.Manifest
import android.content.ComponentName
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.android.synthetic.main.no_internet.*
import kotlinx.android.synthetic.main.problem_detail.*
import kotlinx.android.synthetic.main.server_not_responding.*
import lt.vilnius.tvarkau.ProblemsMapActivity
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.base.BaseRobolectricTest
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.wrapInResponse
import org.assertj.android.api.Assertions.assertThat
import org.junit.Test
import org.robolectric.Shadows
import javax.inject.Inject

/**
 * @author Martynas Jurkus
 */
class ProblemDetailFragmentTest : BaseRobolectricTest() {

    @Inject
    lateinit var api: LegacyApiService

    override fun setUp() {
        super.setUp()
        activity.getTestComponent().inject(this)
    }

    @Test
    fun problemWithoutAnswer_displayCorrectLayout() {
        val problem = Problem()
        whenever(api.getProblem(any())).thenReturn(problem.wrapInResponse)

        val fragment = initFragment()

        assertThat(fragment.problem_detail_view).isVisible
        assertThat(fragment.no_internet_view).isGone
        assertThat(fragment.server_not_responding_view).isGone
        assertThat(fragment.problem_answer_block).isGone

        verify(api).getProblem(any())
    }

    @Test
    fun clickOnAddress_startProblemMapsActivity() {
        grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        val problemId = "problem id"
        val problem = Problem(id = problemId)
        whenever(api.getProblem(any())).thenReturn(problem.wrapInResponse)

        val fragment = initFragment()
        fragment.problem_address.performClick()

        val shadowActivity = Shadows.shadowOf(activity)
        val intent = shadowActivity.peekNextStartedActivity()

        assertThat(intent).hasComponent(ComponentName(activity, ProblemsMapActivity::class.java))
    }

    private fun grantPermissions(vararg permissionNames: String) {
        val application = Shadows.shadowOf(activity.application)
        application.grantPermissions(*permissionNames)
    }

    private fun initFragment(): ProblemDetailFragment {
        return ProblemDetailFragment.getInstance("problem_id").apply {
            setFragment(this)
        }
    }
}

