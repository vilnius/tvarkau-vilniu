package lt.vilnius.tvarkau.api

import lt.vilnius.tvarkau.auth.ApiToken
import lt.vilnius.tvarkau.prefs.ObjectPreference
import lt.vilnius.tvarkau.prefs.Preferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ApiHeadersInterceptor @Inject constructor(
        @Named(Preferences.API_TOKEN)
        private val apiToken: ObjectPreference<ApiToken>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        appendToken(chain, requestBuilder)

        return chain.proceed(requestBuilder.build())
    }

    private fun appendToken(chain: Interceptor.Chain, requestBuilder: Request.Builder) {
        val containsTokenHeader = chain.request().headers().names().contains(HTTP_HEADER_OAUTH)

        if (apiToken.isSet() && !containsTokenHeader) {
            applyToken(requestBuilder)
        }
    }

    private fun applyToken(requestBuilder: Request.Builder) {
        val tokenValue = apiToken.get()
        requestBuilder.addHeader(HTTP_HEADER_OAUTH, formatTokenForHeader(tokenValue.accessToken))
    }

    private fun formatTokenForHeader(token: String) = "Bearer $token"

    companion object {
        const val HTTP_HEADER_OAUTH = "Authorization"
    }
}
