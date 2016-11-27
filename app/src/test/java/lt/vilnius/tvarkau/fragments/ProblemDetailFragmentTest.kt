package lt.vilnius.tvarkau.fragments

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.android.synthetic.main.no_internet.*
import kotlinx.android.synthetic.main.problem_detail.*
import kotlinx.android.synthetic.main.server_not_responding.*
import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.base.BaseRobolectricTest
import lt.vilnius.tvarkau.entity.Problem
import org.assertj.android.api.Assertions.assertThat
import org.junit.Test
import rx.Observable
import javax.inject.Inject

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

    private fun initFragment(): ProblemDetailFragment {
        return ProblemDetailFragment.getInstance("problem_id").apply {
            setFragment(this)
        }
    }
}

