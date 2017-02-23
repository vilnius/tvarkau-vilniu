package lt.vilnius.tvarkau.fragments.presenters

import lt.vilnius.tvarkau.fragments.interactors.MultipleReportsMapInteractor
import lt.vilnius.tvarkau.fragments.views.MultipleProblemsMapView
import rx.Scheduler
import rx.Subscription
import timber.log.Timber

class MultipleReportsMapPresenterImpl(
        private val interactor: MultipleReportsMapInteractor,
        private val uiScheduler: Scheduler,
        private val view: MultipleProblemsMapView
) : MultipleReportsMapPresenter {

    private val subscriptions = mutableListOf<Subscription>()

    override fun onAttach() {
        interactor.getReports()
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnUnsubscribe { view.hideProgress() }
                .subscribe({
                    view.addMarkers(it)
                }, {
                    view.showError()
                    Timber.e(it)
                })
    }

    override fun onDetach() {
        subscriptions.forEach(Subscription::unsubscribe)
    }
}