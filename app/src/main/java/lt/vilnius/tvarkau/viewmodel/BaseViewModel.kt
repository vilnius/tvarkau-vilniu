package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import lt.vilnius.tvarkau.repository.NetworkState
import lt.vilnius.tvarkau.utils.ProgressState

open class BaseViewModel : ViewModel() {

    protected val _errorEvents = SingleLiveEvent<Throwable>()
    val errorEvents: LiveData<Throwable>
        get() = _errorEvents

    private val _progressState = SingleLiveEvent<ProgressState>()
    val progressState: LiveData<ProgressState>
        get() = _progressState

    protected val _networkState = MutableLiveData<NetworkState>()

    private val disposable = CompositeDisposable()


    fun <T : Any> Single<T>.bindProgress(): Single<T> {
        return doOnSubscribe { _progressState.value = ProgressState.show }
            .doFinally { _progressState.value = ProgressState.hide }
    }

    fun Disposable.bind() {
        disposable.add(this)
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}
