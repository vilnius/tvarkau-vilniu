package lt.vilnius.tvarkau.auth

import com.nhaarman.mockito_kotlin.*
import com.vinted.preferx.ObjectPreference
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.api.ApiHeadersInterceptor
import lt.vilnius.tvarkau.api.ApiHeadersInterceptor.Companion.HTTP_HEADER_OAUTH
import lt.vilnius.tvarkau.prefs.AppPreferences
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Test
import java.util.concurrent.TimeUnit

class OauthTokenRefresherTest {

    val oldToken = ApiToken(accessToken = "old")
    val newToken = ApiToken(accessToken = "new")

    private val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return if (request.getHeader(HTTP_HEADER_OAUTH) == formatTokenForHeader(oldToken.accessToken)) {
                MockResponse().setResponseCode(401)
            } else if (request.getHeader(HTTP_HEADER_OAUTH) == formatTokenForHeader(newToken.accessToken)) {
                MockResponse().setResponseCode(200)
            } else {
                throw RuntimeException("Token is not supported")
            }
        }
    }

    private val webServer: MockWebServer = MockWebServer().apply {
        setDispatcher(dispatcher)
    }

    private val apiTokenPref = ApiTokenPref(oldToken)

    private val appPreferences: AppPreferences = mock {
        on { apiToken } doReturn apiTokenPref
    }

    private val sessionToken: SessionToken = mock {
        on { refreshCurrentToken(any()) } doAnswer {
            Completable.complete().delay(100, TimeUnit.MILLISECONDS).doOnComplete {
                apiTokenPref.set(newToken)
            }
        }
    }

    private val apiHeadersInterceptor = ApiHeadersInterceptor(appPreferences)

    val fixture = OauthTokenRefresher(appPreferences, sessionToken, apiHeadersInterceptor)

    private val client = OkHttpClient.Builder()
            .addInterceptor(apiHeadersInterceptor)
            .addInterceptor(fixture)
            .build()

    private val ioScheduler = Schedulers.io()

    @Test
    fun multipleRequests_onlyOneTokenRefresh() {
        Flowable.range(1, 50).flatMap({
            Flowable.fromCallable {
                val request = Request.Builder().url(webServer.url("/")).build()
                client.newCall(request).execute()
            }.subscribeOn(ioScheduler)
        }, true, 50, 50).blockingLast()

        verify(sessionToken, only()).refreshCurrentToken(oldToken)
    }

    @Test
    fun multipleRequests_allSuccess() {
        Flowable.range(1, 50).flatMap({
            Flowable.fromCallable {
                val request = Request.Builder().url(webServer.url("/")).build()
                client.newCall(request).execute()
            }.subscribeOn(ioScheduler)
        }, true, 50, 50).filter { it.code() == 200 }.test().await().assertValueCount(50)
    }

    @Test
    fun multipleRequests_failedRefreshToken() {
        val error = RuntimeException("failed")
        whenever(sessionToken.refreshCurrentToken(any())).thenReturn(Completable.error(error))

        Flowable.range(1, 50).flatMap({
            Flowable.fromCallable {
                val request = Request.Builder().url(webServer.url("/")).build()
                client.newCall(request).execute()
            }.subscribeOn(ioScheduler)
        }, true, 50, 50)
                .timeout(10000, TimeUnit.MILLISECONDS)
                .filter { it.code() == 500 }
                .test().await().assertValueCount(50)
    }

    @Test
    fun multipleRequests_refreshServerIsRestoredEventually() {
        val error = RuntimeException("failed")
        whenever(sessionToken.refreshCurrentToken(any())).thenReturn(Completable.error(error))

        //add noise before and in the middle.
        Flowable.range(1, 50)
                .delay {
                    Flowable.just(0).delay(it / 30L, TimeUnit.SECONDS)
                }
                .flatMap({
                    Flowable.fromCallable {
                        val request = Request.Builder().url(webServer.url("/")).build()
                        client.newCall(request).execute()
                    }.subscribeOn(ioScheduler)
                }, true, 50, 50)
                .test()

        //server is revived
        whenever(sessionToken.refreshCurrentToken(any())).doAnswer {
            Completable.complete().delay(100, TimeUnit.MILLISECONDS).doOnComplete {
                apiTokenPref.set(newToken)
            }
        }

        //check is all next request succeed
        Flowable.range(1, 50).flatMap({
            Flowable.fromCallable {
                val request = Request.Builder().url(webServer.url("/")).build()
                client.newCall(request).execute()
            }.subscribeOn(ioScheduler)
        }, true, 50, 50).filter { it.code() == 200 }.test().await().assertValueCount(50)
    }

    fun formatTokenForHeader(token: String) = "Bearer $token"

    class ApiTokenPref(private var current: ApiToken) : ObjectPreference<ApiToken> {
        override fun set(value: ApiToken, commit: Boolean) {
            current = value
        }

        override fun get(): ApiToken = current

        override fun delete() {
            throw UnsupportedOperationException("not implemented")
        }

        override fun isSet() = true

        override val onChangeObservable: Observable<ApiToken>
            get() = TODO("not implemented")
    }
}