package lt.vilnius.tvarkau.auth

import lt.vilnius.tvarkau.api.ApiHeadersInterceptor
import lt.vilnius.tvarkau.api.ApiHeadersInterceptor.Companion.HTTP_HEADER_OAUTH
import lt.vilnius.tvarkau.prefs.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.net.HttpURLConnection
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class OauthTokenRefresher @Inject constructor(
        private val appPreferences: AppPreferences,
        private val sessionToken: SessionToken,
        private val apiHeadersInterceptor: ApiHeadersInterceptor
) : Interceptor {
    private val isRefreshing = AtomicBoolean(false)

    private val lock = Object()

    private var counter = 0

    private val queue = mutableListOf<CallbackLock<CallbackStatus>>()

    private fun applyToken(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder().removeHeader(HTTP_HEADER_OAUTH)
        apiHeadersInterceptor.applyToken(requestBuilder)
        return chain.proceed(requestBuilder.build())
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val counter = this.counter++

        if (!appPreferences.apiToken.isSet() || response.code() != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return response
        }

        Timber.d("error $counter")

        val localLock = synchronized(lock) {
            val isRefreshingInAnotherThread = !isRefreshing.compareAndSet(false, true)
            if (isRefreshingInAnotherThread) {
                CallbackLock<CallbackStatus>().apply {
                    queue += this
                }
            } else {
                null
            }
        }

        if (localLock != null) {
            Timber.d("is refreshing in another thread $counter")
            val result = localLock.block()
            Timber.d("unlocked: $counter with result: $result")

            return if (result == CallbackStatus.ERROR) {
                response.newBuilder().code(500).build()
            } else {
                applyToken(chain)
            }
        }

        Timber.d("refresh $counter")
        val error = sessionToken.refreshCurrentToken(appPreferences.apiToken.get()).blockingGet()

        synchronized(lock) {
            val status = if (error != null) CallbackStatus.ERROR else CallbackStatus.SUCCESS
            Timber.d("unlock $counter with status: $status")
            isRefreshing.set(false)
            queue.forEach { it.sendAndUnlock(status) }
            queue.clear()
        }

        if (error != null) {
            Timber.d("error $counter: $error")
            return response.newBuilder().code(500).build()
        }

        return applyToken(chain)
    }

    enum class CallbackStatus {
        SUCCESS, ERROR
    }

    class CallbackLock<T : Any> {
        @Volatile
        private var result: T? = null

        private val lock = Object()

        fun sendAndUnlock(result: T) {
            synchronized(lock) {
                if (this.result != null) throw IllegalStateException("Unlocked already")
                this.result = result
                lock.notifyAll()
            }
        }

        fun block(): T {
            synchronized(lock) {
                while (result == null) {
                    try {
                        lock.wait()
                    } catch (e: InterruptedException) {
                        //InterruptedException is using to stop sleep.
                    }
                }
            }
            return result!!
        }
    }
}
