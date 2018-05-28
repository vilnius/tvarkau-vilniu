package lt.vilnius.tvarkau.fragments.interactors

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.Single.just
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.wrapInResponse
import org.junit.Test
import org.threeten.bp.LocalDateTime
import kotlin.test.assertEquals

/**
 * @author Martynas Jurkus
 */
class MyReportListInteractorTest {

    private val api = mock<LegacyApiService>()
    private val idProvider = mock<MyReportsInteractor>()

    private val fixture = MyReportListInteractor(
            api,
            idProvider,
            Schedulers.trampoline()
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
                .thenReturn(Single.error(RuntimeException("some api error occurred")))

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
                .thenReturn(Single.just(ApiResponse<Problem>()))
                .thenReturn(problems[1].wrapInResponse)

        fixture.getProblems(1)
                .test()
                .assertResult(problems)

        verify(api, times(3)).getProblem(any())
        verify(idProvider).removeReportId("2")
    }

    @Test
    fun getReports_returnsSortedByEntryDate() {
        val problemsIds = listOf("1", "2", "3")
        val problems = listOf(
                Problem(
                        id = "any",
                        entryDate = LocalDateTime.of(2017, 1, 1, 12, 2)
                ),
                Problem(
                        id = "any_1",
                        entryDate = LocalDateTime.of(2017, 1, 1, 12, 3)
                ),
                Problem(
                        id = "any_2",
                        entryDate = LocalDateTime.of(2017, 1, 1, 12, 1)
                )
        )

        whenever(idProvider.getReportIds()).thenReturn(just(problemsIds))
        whenever(api.getProblem(any()))
                .thenReturn(problems[0].wrapInResponse)
                .thenReturn(problems[1].wrapInResponse)
                .thenReturn(problems[2].wrapInResponse)

        val sorted = fixture.getProblems(1)
                .test()
                .values()
                .first()

        assertEquals(sorted[0], problems[1])
    }
}