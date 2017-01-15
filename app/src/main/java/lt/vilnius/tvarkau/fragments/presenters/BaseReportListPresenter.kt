package lt.vilnius.tvarkau.fragments.presenters

import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProviderImpl.NetworkConnectivityError
import lt.vilnius.tvarkau.fragments.views.ReportListView
import rx.Single
import timber.log.Timber

/**
 * @author Martynas Jurkus
 */
abstract class BaseReportListPresenter(
        private val connectivityProvider: ConnectivityProvider,
        protected val view: ReportListView
) : ProblemListPresenter {

    fun withConnectivityCheck(): Single<Boolean> {
        return connectivityProvider.ensureConnected()
    }

    fun handleError(error: Throwable, lastPage: Int) {
        Timber.e(error)

        when (error) {
            is NetworkConnectivityError -> view.showNetworkError(lastPage)
            else -> view.showError(error)
        }
    }
}