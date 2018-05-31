package lt.vilnius.tvarkau.fragments.presenters

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import lt.vilnius.tvarkau.fragments.interactors.ReportListInteractor
import lt.vilnius.tvarkau.fragments.views.ReportListView

/**
 * @author Martynas Jurkus
 */
class MyReportListPresenterImpl(
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
                .doOnSubscribe { view.showProgress() }
                .doFinally { view.hideProgress() }
                .doFinally { view.hideLoader() }
                .subscribe({
                    if (it.isEmpty()) {
                        view.showEmptyState()
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