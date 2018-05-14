package lt.vilnius.tvarkau.auth

import ca.mimic.oauth2library.OAuth2Client
import ca.mimic.oauth2library.OAuthResponse
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import lt.vilnius.tvarkau.dagger.module.GuestToken
import lt.vilnius.tvarkau.dagger.module.RefreshToken
import lt.vilnius.tvarkau.data.GsonSerializer
import lt.vilnius.tvarkau.prefs.AppPreferences
import timber.log.Timber
import javax.inject.Inject

class SessionTokenImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        @GuestToken
        private val guestOAuth: OAuth2Client.Builder,
        @RefreshToken
        private val refreshOAuthToken: OAuth2Client.Builder,
        private val gsonSerializer: GsonSerializer
) : SessionToken {

    override fun refreshGuestToken(): Completable {
        return Completable.create { emitter ->
            guestOAuth.build()
                    .requestAccessToken {
                        handleAccessToken(it, emitter)
                    }
        }
    }

    private fun handleAccessToken(response: OAuthResponse, emitter: CompletableEmitter) {
        if (response.isSuccessful) {
            appPreferences.apiToken.set(gsonSerializer.fromJson(response.body, ApiToken::class.java))
            Timber.d("Token was set to ${appPreferences.apiToken.get()}")
            emitter.onComplete()
        } else {
            if (response.oAuthError?.errorDescription.isNullOrEmpty()) {
                emitter.tryOnError(IllegalStateException("Error code: ${response.code} Error message: ${response.httpResponse?.message()}"))
            } else {
                emitter.tryOnError(OAuthException(response.oAuthError.errorDescription))
            }
        }
    }

    override fun refreshCurrentToken(token: ApiToken): Completable {
        return Completable.create { emitter ->
            refreshOAuthToken
                    .parameters(mapOf("refresh_token" to token.refreshToken))
                    .build()
                    .requestAccessToken {
                        if (!it.isSuccessful && it.oAuthError.error == "invalid_grant") {
                            Timber.w("LOGOUT USER!!!")
                        }
                        handleAccessToken(it, emitter)
                    }
        }
    }
}
