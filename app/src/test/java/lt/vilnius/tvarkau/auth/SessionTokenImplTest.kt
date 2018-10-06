package lt.vilnius.tvarkau.auth

import ca.mimic.oauth2library.OAuth2Client
import ca.mimic.oauth2library.OAuthError
import ca.mimic.oauth2library.OAuthResponse
import ca.mimic.oauth2library.OAuthResponseCallback
import com.google.gson.GsonBuilder
import com.nhaarman.mockito_kotlin.KStubbing
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.vinted.preferx.ObjectPreference
import lt.vilnius.tvarkau.data.GsonSerializerImpl
import lt.vilnius.tvarkau.prefs.AppPreferences
import org.junit.Test

class SessionTokenImplTest {

    private val tokenPref = mock<ObjectPreference<ApiToken>>()
    private val oAuthClient = mock<OAuth2Client>()
    private val guestOAuth = mock<OAuth2Client.Builder> {
        on { build() } doReturn oAuthClient
    }
    private val refreshTokenOauth = mock<OAuth2Client.Builder> {
        on { build() } doReturn oAuthClient
    }
    private val thirdPartyToken = mock<OAuth2Client.Builder> {
        on { build() } doReturn oAuthClient
    }
    private val gson = GsonBuilder().create()
    private val gsonSerializer = GsonSerializerImpl(gson)
    private val appPreferences = mock<AppPreferences> {
        on { apiToken } doReturn tokenPref
    }

    private val fixture = SessionTokenImpl(
        appPreferences = appPreferences,
        guestOAuth = guestOAuth,
        refreshOAuthToken = refreshTokenOauth,
        thirdPartyToken = thirdPartyToken,
        gsonSerializer = gsonSerializer
    )

    @Test
    fun retrievePublicTokenSuccess_saveToken() {
        mockOAuthResponse(success = true, token = "token")

        val result = fixture.refreshGuestToken().test()

        result.assertComplete()
        verify(tokenPref).set(argThat { accessToken == "token" }, eq(false))
    }

    @Test
    fun retrievePublicTokenError_error() {
        mockOAuthResponse(success = false)

        val result = fixture.refreshGuestToken().test()

        result.assertError { true }
    }

    @Test
    fun refreshCurrentToken_success() {
        whenever(refreshTokenOauth.parameters(any())).thenReturn(refreshTokenOauth)
        val currentToken = ApiToken(accessToken = "currentToken")

        mockOAuthResponse(true, "newToken")

        val result = fixture.refreshCurrentToken(currentToken).test()

        result.assertComplete()
        verify(tokenPref).set(argThat { accessToken == "newToken" }, any())
    }

    @Test
    fun refreshCurrentToken_commonError() {
        whenever(refreshTokenOauth.parameters(any())).thenReturn(refreshTokenOauth)
        val currentToken = ApiToken(accessToken = "currentToken")

        val oAuthError: OAuthError = mock {
            on { error } doReturn "random"
        }

        mockOAuthResponse {
            on { isSuccessful } doReturn false
            on { this.oAuthError } doReturn oAuthError
        }

        val result = fixture.refreshCurrentToken(currentToken).test().await()

        result.assertError(Throwable::class.java)
        verify(tokenPref, never()).set(any(), any())
    }

    private fun mockOAuthResponse(
        success: Boolean,
        token: String = "someOtherToken"
    ) {
        mockOAuthResponse {
            on { isSuccessful } doReturn success
            on { accessToken } doReturn token
            on { body } doReturn gsonSerializer.toJson(ApiToken(accessToken = token))
        }
    }

    private inline fun mockOAuthResponse(stubbing: KStubbing<OAuthResponse>.(OAuthResponse) -> Unit) {
        val response: OAuthResponse = mock(stubbing = stubbing)
        whenever(oAuthClient.requestAccessToken(any())).thenAnswer {
            (it.arguments[0] as OAuthResponseCallback).onResponse(response)
        }
    }
}
