package lt.vilnius.tvarkau.fragments.presenters

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import lt.vilnius.tvarkau.fragments.interactors.MultipleReportsMapInteractor
import lt.vilnius.tvarkau.fragments.views.MultipleProblemsMapView
import timber.log.Timber

class MultipleReportsMapPresenterImpl(
        private val interactor: MultipleReportsMapInteractor,
        private val uiScheduler: Scheduler,
        private val connectivityProvider: ConnectivityProvider,
        private val view: MultipleProblemsMapView
) : MultipleReportsMapPresenter {

    private val disposable = CompositeDisposable()

    override fun onAttach() {
        connectivityProvider.ensureConnected()
                .flatMap { interactor.getReports() }
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doFinally { view.hideProgress() }
                .subscribe({
                    view.addMarkers(it)
                }, {
                    handleErrors(it)
                }).apply { disposable.add(this) }
    }

    private fun handleErrors(throwable: Throwable) {
        when (throwable) {
            is ConnectivityProviderImpl.NetworkConnectivityError -> view.showNetworkError()
            else -> view.showError()
        }

        Timber.e(throwable)
    }

    override fun onDetach() {
        disposable.clear()
    }
}