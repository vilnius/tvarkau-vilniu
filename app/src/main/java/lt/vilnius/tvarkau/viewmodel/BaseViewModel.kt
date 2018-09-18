package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {

    protected val _errorEvents = SingleLiveEvent<Throwable>()
    val errorEvents: LiveData<Throwable>
        get() = _errorEvents

    private val disposable = CompositeDisposable()

    fun Disposable.bind() {
        disposable.add(this)
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}
