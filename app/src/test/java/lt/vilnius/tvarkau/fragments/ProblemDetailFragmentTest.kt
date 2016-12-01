package lt.vilnius.tvarkau.fragments

import android.Manifest
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.android.synthetic.main.no_internet.*
import kotlinx.android.synthetic.main.problem_detail.*
import kotlinx.android.synthetic.main.server_not_responding.*
import lt.vilnius.tvarkau.ProblemsMapActivity
import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.base.BaseRobolectricTest
import lt.vilnius.tvarkau.entity.Problem
import org.assertj.android.api.Assertions.assertThat
import org.junit.Test
import org.robolectric.Shadows
import rx.Observable
import javax.inject.Inject
import kotlin.test.assertEquals

/**
 * @author Martynas Jurkus
 */
class ProblemDetailFragmentTest : BaseRobolectricTest() {

    @Inject
    lateinit var api: LegacyApiService

    private val Problem.wrapInResponse: Observable<ApiResponse<Problem>>
        get() {
            val response = ApiResponse<Problem>()
            response.result = this
            return Observable.just(response)
        }

    private val clipboard by lazy {
        Shadows.shadowOf(activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
    }

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
    fun allowCopy_problemId() {
        val problemId = "problem id"
        val problem = Problem(id = problemId)
        whenever(api.getProblem(any())).thenReturn(problem.wrapInResponse)

        val fragment = initFragment()
        fragment.problem_id.performLongClick()

        assertEquals(problemId, clipboard.primaryClip.getItemAt(0).text)
    }

    @Test
    fun allowCopy_problemTitle() {
        val problemTitle = "problem title"
        val problem = Problem(typeName = problemTitle)
        whenever(api.getProblem(any())).thenReturn(problem.wrapInResponse)

        val fragment = initFragment()
        fragment.problem_title.performLongClick()

        assertEquals(problemTitle, clipboard.primaryClip.getItemAt(0).text)
    }

    @Test
    fun allowCopy_problemAddress() {
        val address = "Gedimino pr. 11"
        val problem = Problem(address = address)
        whenever(api.getProblem(any())).thenReturn(problem.wrapInResponse)

        val fragment = initFragment()
        fragment.problem_address.performLongClick()

        assertEquals(address, clipboard.primaryClip.getItemAt(0).text)
    }

    @Test
    fun allowCopy_problemAnswerDate() {
        val answerDate = "some date"
        val problem = Problem(
                answer = "Answer",
                completeDate = answerDate
        )
        whenever(api.getProblem(any())).thenReturn(problem.wrapInResponse)

        val fragment = initFragment()
        fragment.problem_answer_date.performLongClick()

        assertEquals(answerDate, clipboard.primaryClip.getItemAt(0).text)
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

