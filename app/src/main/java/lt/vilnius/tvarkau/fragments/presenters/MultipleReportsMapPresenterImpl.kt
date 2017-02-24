package lt.vilnius.tvarkau.fragments.presenters

import lt.vilnius.tvarkau.fragments.interactors.MultipleReportsMapInteractor
import lt.vilnius.tvarkau.fragments.views.MultipleProblemsMapView
import rx.Scheduler
import rx.Subscription
import timber.log.Timber

class MultipleReportsMapPresenterImpl(
        private val interactor: MultipleReportsMapInteractor,
        private val uiScheduler: Scheduler,
        private val connectivityProvider: ConnectivityProvider,
        private val view: MultipleProblemsMapView
) : MultipleReportsMapPresenter {

    private val subscriptions = mutableListOf<Subscription>()

    override fun onAttach() {
        connectivityProvider.ensureConnected()
                .flatMap { interactor.getReports() }
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnUnsubscribe { view.hideProgress() }
                .subscribe({
                    view.addMarkers(it)
                }, {
                    handleErrors(it)
                }).apply { subscriptions += this }
    }

    private fun handleErrors(throwable: Throwable) {
        when (throwable) {
            is ConnectivityProviderImpl.NetworkConnectivityError -> view.showNetworkError()
            else -> view.showError()
        }

        Timber.e(throwable)
    }

    override fun onDetach() {
        subscriptions.forEach(Subscription::unsubscribe)
    }
}