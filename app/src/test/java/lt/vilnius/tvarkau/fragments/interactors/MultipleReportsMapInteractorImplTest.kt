package lt.vilnius.tvarkau.fragments.interactors

import com.nhaarman.mockito_kotlin.*
import com.vinted.preferx.StringPreference
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.RefreshReportFilterEvent
import lt.vilnius.tvarkau.rx.RxBus
import lt.vilnius.tvarkau.wrapInResponse
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author Martynas Jurkus
 */
class MultipleReportsMapInteractorImplTest {

    private val api = mock<LegacyApiService>()
    private val reportType = mock<StringPreference>()
    private val reportStatus = mock<StringPreference>()
    private val allReportsTitle = "All"

    private val fixture: MultipleReportsMapInteractor =
            MultipleReportsMapInteractorImpl(
                    api,
                    Schedulers.immediate(),
                    reportType,
                    reportStatus,
                    allReportsTitle
            )

    private val reports = listOf(Problem(id = "1"), Problem(id = "2"))

    @Before
    fun setUp() {
        whenever(reportType.get()).thenReturn("")
        whenever(reportStatus.get()).thenReturn("")
        whenever(api.getProblems(any())).thenReturn(reports.wrapInResponse)
    }

    @Test
    fun emptyCache_apiHit() {
        fixture.getReports()
                .test()
                .assertValue(reports)

        verify(api).getProblems(any())
    }

    @Test
    fun cacheHit_noApiRequest() {
        whenever(api.getProblems(any()))
                .thenReturn(reports.wrapInResponse)
                .thenReturn(Observable.error(IllegalStateException("Unexpected API call")))

        fixture.getReports().subscribe()

        fixture.getReports()
                .test()
                .assertNoErrors()
                .assertValue(reports)
    }

    @Test
    fun emptyCache_apiHit_noResults_error() {
        whenever(api.getProblems(any())).thenReturn(listOf<Problem>().wrapInResponse)

        fixture.getReports()
                .test()
                .assertError(MultipleReportsMapInteractorImpl.NoMapReportsError::class.java)

        verify(api).getProblems(any())
    }

    @Test
    fun mapRefresh_cacheCleared() {
        fixture.getReports().subscribe()
        RxBus.publish(RefreshReportFilterEvent())
        fixture.getReports().subscribe()

        verify(api, times(2)).getProblems(any())
    }
}