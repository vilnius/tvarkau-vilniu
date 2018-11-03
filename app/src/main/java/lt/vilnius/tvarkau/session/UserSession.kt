package lt.vilnius.tvarkau.session

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.CompletableSubject
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.entity.User
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(
    private val api: TvarkauMiestaApi
) {

    init {
        Timber.d("Creating user session")
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun refreshUser(): Completable {
        val result = CompletableSubject.create()

        api.getCurrentUser()
            .map { it.user }
            .subscribeBy(
                onSuccess = {
                    _user.postValue(it)
                    result.onComplete()
                },
                onError = { result.onError(it) }
            )

        return result
    }
}
