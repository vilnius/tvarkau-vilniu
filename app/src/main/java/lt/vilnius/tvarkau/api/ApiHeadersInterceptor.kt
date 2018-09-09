package lt.vilnius.tvarkau.api

import lt.vilnius.tvarkau.prefs.AppPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiHeadersInterceptor @Inject constructor(
    private val appPreferences: AppPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        appendToken(chain, requestBuilder)

        return chain.proceed(requestBuilder.build())
    }

    private fun appendToken(chain: Interceptor.Chain, requestBuilder: Request.Builder) {
        val containsTokenHeader = chain.request().headers().names().contains(HTTP_HEADER_OAUTH)

        if (appPreferences.apiToken.isSet() && !containsTokenHeader) {
            applyToken(requestBuilder)
        }
    }

    fun applyToken(requestBuilder: Request.Builder) {
        val tokenValue = appPreferences.apiToken.get()
        requestBuilder.addHeader(HTTP_HEADER_OAUTH, formatTokenForHeader(tokenValue.accessToken))
        requestBuilder.addHeader(HTTP_HEADER_CITY, appPreferences.selectedCity.get().id.toString())
    }

    private fun formatTokenForHeader(token: String) = "Bearer $token"

    companion object {
        const val HTTP_HEADER_OAUTH = "Authorization"
        const val HTTP_HEADER_CITY = "X-City-ID"
    }
}
