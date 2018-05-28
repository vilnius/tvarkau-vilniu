package lt.vilnius.tvarkau.fragments.presenters

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import lt.vilnius.tvarkau.fragments.interactors.ReportListInteractor
import lt.vilnius.tvarkau.fragments.views.ReportListView

/**
 * @author Martynas Jurkus
 */
class AllReportsListPresenterImpl(
        private val interactor: ReportListInteractor,
        private val uiScheduler: Scheduler,
        view: ReportListView,
        connectivityProvider: ConnectivityProvider
) : BaseReportListPresenter(connectivityProvider, view) {

    private val disposable = CompositeDisposable()

    override fun onAttach() {
    }

    override fun getReportsForPage(page: Int) {
        withConnectivityCheck()
                .flatMap { interactor.getProblems(page) }
                .observeOn(uiScheduler)
                .doOnSubscribe { if (page == 0) view.showProgress() }
                .doFinally { view.hideProgress() }
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
                }).apply { disposable.add(this) }
    }

    override fun onDetach() {
        disposable.clear()
    }
}