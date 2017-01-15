package lt.vilnius.tvarkau.fragments.interactors

import com.nhaarman.mockito_kotlin.*
import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.wrapInResponse
import org.junit.Test
import rx.Observable
import rx.Single.just
import rx.schedulers.Schedulers

/**
 * @author Martynas Jurkus
 */
class MyReportListInteractorTest {

    val api = mock<LegacyApiService>()
    val idProvider = mock<MyReportsInteractor>()

    val fixture: ReportListInteractor = MyReportListInteractor(
            api,
            idProvider,
            Schedulers.immediate()
    )

    @Test
    fun noProblems_noApiCalls_emptyResults() {
        whenever(idProvider.getReportIds()).thenReturn(just(emptyList()))

        fixture.getProblems(1)
                .test()
                .assertResult(emptyList())

        verifyZeroInteractions(api)
        verify(idProvider, never()).removeReportId(any())
    }

    @Test
    fun getProblems_apiCalled_allReturned() {
        val problemsIds = listOf("1", "2")
        val problems = listOf(Problem(id = "any"), Problem(id = "any_1"))

        whenever(idProvider.getReportIds()).thenReturn(just(problemsIds))
        whenever(api.getProblem(any()))
                .thenReturn(problems[0].wrapInResponse)
                .thenReturn(problems[1].wrapInResponse)

        fixture.getProblems(1)
                .test()
                .assertResult(problems)

        verify(api, times(2)).getProblem(any())
        verify(idProvider, never()).removeReportId(any())
    }

    @Test
    fun getProblems_oneApiCallFailed_dataReturned() {
        val problemsIds = listOf("1", "2")
        val problems = listOf(Problem(id = "any"), Problem(id = "any_1"))

        whenever(idProvider.getReportIds()).thenReturn(just(problemsIds))
        whenever(api.getProblem(any()))
                .thenReturn(problems[0].wrapInResponse)
                .thenReturn(Observable.error(RuntimeException("some api error occurred")))

        fixture.getProblems(1)
                .test()
                .assertResult(problems.dropLast(1))

        verify(api, times(2)).getProblem(any())
        verify(idProvider, never()).removeReportId(any())
    }

    @Test
    fun getProblems_oneNotFound_keyRemoved() {
        val problemsIds = listOf("1", "2", "3")
        val problems = listOf(Problem(id = "any"), Problem(id = "any_1"))

        whenever(idProvider.getReportIds()).thenReturn(just(problemsIds))
        whenever(api.getProblem(any()))
                .thenReturn(problems[0].wrapInResponse)
                .thenReturn(Observable.just(ApiResponse<Problem>()))
                .thenReturn(problems[1].wrapInResponse)

        fixture.getProblems(1)
                .test()
                .assertResult(problems)

        verify(api, times(3)).getProblem(any())
        verify(idProvider).removeReportId("2")
    }
}