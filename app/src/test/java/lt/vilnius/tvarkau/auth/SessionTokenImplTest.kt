package lt.vilnius.tvarkau.auth

import ca.mimic.oauth2library.OAuth2Client
import ca.mimic.oauth2library.OAuthResponse
import ca.mimic.oauth2library.OAuthResponseCallback
import com.google.gson.GsonBuilder
import com.nhaarman.mockito_kotlin.*
import lt.vilnius.tvarkau.data.GsonSerializerImpl
import lt.vilnius.tvarkau.prefs.ObjectPreference
import org.junit.Test

class SessionTokenImplTest {

    private val tokenPref = mock<ObjectPreference<ApiToken>>()
    private val oAuthClient = mock<OAuth2Client>()
    private val guestOAuth = mock<OAuth2Client.Builder> {
        on { build() } doReturn oAuthClient
    }
    private val gson = GsonBuilder().create()
    private val gsonSerializer = GsonSerializerImpl(gson)
    private val fixture = SessionTokenImpl(tokenPref, guestOAuth, gsonSerializer)

    @Test
    fun retrievePublicTokenSuccess_saveToken() {
        mockOAuthResponse(success = true, token = "token")

        val result = fixture.refreshGuestToken().test()

        result.assertComplete()
        verify(tokenPref).set(argThat { accessToken == "token" })
    }

    @Test
    fun retrievePublicTokenError_error() {
        mockOAuthResponse(success = false)

        val result = fixture.refreshGuestToken().test()

        result.assertError { true }
    }

    private fun mockOAuthResponse(
            success: Boolean,
            token: String = "someOtherToken"
    ) {
        val gson = GsonBuilder().create()
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