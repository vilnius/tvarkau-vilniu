package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations.map
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import lt.vilnius.tvarkau.auth.SessionToken
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.entity.SocialNetworkUser
import lt.vilnius.tvarkau.entity.User
import lt.vilnius.tvarkau.prefs.AppPreferences
import lt.vilnius.tvarkau.repository.NetworkState
import lt.vilnius.tvarkau.session.UserSession
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    @UiScheduler
    private val uiScheduler: Scheduler,
    private val sessionToken: SessionToken,
    private val userSession: UserSession,
    private val appPreferences: AppPreferences
) : BaseViewModel() {

    val networkState: LiveData<NetworkState> = map(_networkState) { it }
    val loggedInUser: LiveData<User> = map(userSession.user) { it }

    init {
        if (appPreferences.apiToken.isSet()) {
            refreshUser()
        }
    }

    fun attemptLogin() {
        _errorEvents.value = RuntimeException("Username/Password authentication not yet implemented")
    }

    fun signInWithGoogle(data: Intent?) {
        try {
            val accountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data)
            signInWithExistingGoogleAccount(accountFromIntent.getResult(ApiException::class.java))
        } catch (error: ApiException) {
            Timber.e(error)
            //TODO add error translations
            _errorEvents.value = RuntimeException("OAuth error: ${error.statusCode}")
            //TODO update UI
        }
    }

    fun signInWithExistingGoogleAccount(account: GoogleSignInAccount) {
        val user = SocialNetworkUser.forGoogle(
            token = account.idToken!!,
            email = account.email!!
        )

        refreshTokenAndRetrieveUser(sessionToken.refreshSocialLoginToken(user))
    }

    fun signInGuestUser() {
        refreshTokenAndRetrieveUser(sessionToken.refreshGuestToken())
    }

    private fun refreshUser() {
        userSession.refreshUser()
            .observeOn(uiScheduler)
            .doOnSubscribe { _networkState.value = NetworkState.LOADING }
            .subscribeBy(
                onComplete = {
                    _networkState.value = NetworkState.LOADED
                },
                onError = {
                    _errorEvents.value = it
                    _networkState.value = NetworkState.error(it.message)
                }
            ).bind()
    }

    private fun refreshTokenAndRetrieveUser(tokenCompletable: Completable) {
        tokenCompletable.andThen(userSession.refreshUser())
            .observeOn(uiScheduler)
            .doOnSubscribe { _networkState.value = NetworkState.LOADING }
            .subscribeBy(
                onComplete = {
                    _networkState.value = NetworkState.LOADED
                },
                onError = {
                    _errorEvents.value = it
                    _networkState.value = NetworkState.error(it.message)
                }
            ).bind()
    }
}
