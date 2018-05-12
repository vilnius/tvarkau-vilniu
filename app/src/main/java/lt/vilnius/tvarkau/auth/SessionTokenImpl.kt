package lt.vilnius.tvarkau.auth

import ca.mimic.oauth2library.OAuth2Client
import ca.mimic.oauth2library.OAuthResponse
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import lt.vilnius.tvarkau.dagger.module.GuestToken
import lt.vilnius.tvarkau.data.GsonSerializer
import lt.vilnius.tvarkau.prefs.ObjectPreference
import lt.vilnius.tvarkau.prefs.Preferences
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SessionTokenImpl @Inject constructor(
        @Named(Preferences.API_TOKEN)
        private val apiToken: ObjectPreference<ApiToken>,
        @GuestToken
        private val guestOAuth: OAuth2Client.Builder,
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
            apiToken.set(gsonSerializer.fromJson(response.body, ApiToken::class.java))
            Timber.d("Token was set to ${apiToken.get()}")
            emitter.onComplete()
        } else {
            if (response.oAuthError?.errorDescription.isNullOrEmpty()) {
                emitter.tryOnError(IllegalStateException("Error code: ${response.code} Error message: ${response.httpResponse?.message()}"))
            } else {
                emitter.tryOnError(OAuthException(response.oAuthError.errorDescription))
            }
        }
    }
}
