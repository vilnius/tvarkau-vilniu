package lt.vilnius.tvarkau.fragments.presenters

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.Single.just
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.fragments.interactors.ReportListInteractor
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProviderImpl.NetworkConnectivityError
import lt.vilnius.tvarkau.fragments.views.ReportListView
import org.junit.Test

/**
 * @author Martynas Jurkus
 */
class AllReportsListPresenterImplTest {

    val interactor = mock<ReportListInteractor>()
    val view = mock<ReportListView>()
    val connectivityProvider = mock<ConnectivityProvider> {
        on { ensureConnected() } doReturn just(true)
    }

    val fixture = AllReportsListPresenterImpl(
            interactor,
            Schedulers.trampoline(),
            view,
            connectivityProvider
    )

    val reports = listOf(Problem(id = "1"), Problem(id = "2"))

    @Test
    fun loadReports_success() {
        whenever(interactor.getProblems(any())).thenReturn(just(reports))

        fixture.getReportsForPage(0)

        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).onReportsLoaded(reports)
    }

    @Test
    fun loadReports_noResults_showEmptyState() {
        whenever(interactor.getProblems(any())).thenReturn(just(emptyList()))

        fixture.getReportsForPage(0)

        verify(view).showEmptyState()
        verify(view, never()).onReportsLoaded(any())
    }

    @Test
    fun loadReports_noNetwork_showNetworkError() {
        whenever(connectivityProvider.ensureConnected()).thenReturn(Single.error(NetworkConnectivityError()))

        fixture.getReportsForPage(0)

        verify(view).showNetworkError(0)
        verify(interactor, never()).getProblems(any())
    }

    @Test
    fun loadReports_otherError_showError() {
        val exception = RuntimeException("Some error")
        whenever(interactor.getProblems(0)).thenReturn(Single.error(exception))

        fixture.getReportsForPage(0)

        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).showError(exception)
    }
}