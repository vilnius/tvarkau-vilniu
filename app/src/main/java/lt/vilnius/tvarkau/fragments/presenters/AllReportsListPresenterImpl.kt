package lt.vilnius.tvarkau.fragments.presenters

import lt.vilnius.tvarkau.fragments.interactors.ReportListInteractor
import lt.vilnius.tvarkau.fragments.views.ReportListView
import rx.Scheduler
import rx.Subscription

/**
 * @author Martynas Jurkus
 */
class AllReportsListPresenterImpl(
        private val interactor: ReportListInteractor,
        private val uiScheduler: Scheduler,
        view: ReportListView,
        connectivityProvider: ConnectivityProvider
) : BaseReportListPresenter(connectivityProvider, view) {

    private val subscriptions = mutableListOf<Subscription>()

    override fun onAttach() {
    }

    override fun getReportsForPage(page: Int) {
        withConnectivityCheck()
                .flatMap { interactor.getProblems(page) }
                .observeOn(uiScheduler)
                .doOnSubscribe { if (page == 0) view.showProgress() }
                .doOnUnsubscribe { view.hideProgress() }
                .subscribe({
                    if (it.isEmpty()) {
                        view.hideLoader()
                        if (page == 0) { //only show empty state if initial request returns empty list
                            view.showEmptyState()
                        }
                    } else {
                        view.hideEmptyState()
                        view.onReportsLoaded(it)
                    }
                }, {
                    handleError(it, page)
                }).apply { subscriptions += this }
    }

    override fun onDetach() {
        subscriptions.forEach(Subscription::unsubscribe)
    }
}