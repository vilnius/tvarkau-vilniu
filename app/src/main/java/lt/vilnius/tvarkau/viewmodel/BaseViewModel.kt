package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import lt.vilnius.tvarkau.repository.NetworkState

open class BaseViewModel : ViewModel() {

    protected val _errorEvents = SingleLiveEvent<Throwable>()
    val errorEvents: LiveData<Throwable>
        get() = _errorEvents

    protected val _networkState = MutableLiveData<NetworkState>()

    private val disposable = CompositeDisposable()

    fun Disposable.bind() {
        disposable.add(this)
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}
