package lt.vilnius.tvarkau.dagger.module

import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import lt.vilnius.tvarkau.BuildConfig
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.api.ApiHeadersInterceptor
import lt.vilnius.tvarkau.auth.OauthTokenRefresher
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.dagger.Api
import lt.vilnius.tvarkau.dagger.IoScheduler
import lt.vilnius.tvarkau.fragments.interactors.MultipleReportsMapInteractor
import lt.vilnius.tvarkau.fragments.interactors.MultipleReportsMapInteractorImpl
import lt.vilnius.tvarkau.prefs.AppPreferences
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideMultipleReportsMapInteractor(
        api: LegacyApiService,
        context: TvarkauApplication,
        @IoScheduler ioScheduler: Scheduler,
        appPreferences: AppPreferences
    ): MultipleReportsMapInteractor {
        return MultipleReportsMapInteractorImpl(
            api,
            ioScheduler,
            appPreferences.reportTypeSelectedFilter,
            appPreferences.reportStatusSelectedFilter,
            context.getString(R.string.report_filter_all_report_types)
        )
    }

    @Provides
    @Singleton
    @RawOkHttpClient
    fun provideRawHttpClient(
        cache: Cache,
        headersInterceptor: ApiHeadersInterceptor
    ): OkHttpClient {
        return okhttp3.OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.MINUTES)
            .addNetworkInterceptor(headersInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Api
    internal fun provideOkHttpClient(
        @RawOkHttpClient rawOkHttpClient: OkHttpClient,
        oauthTokenRefresher: OauthTokenRefresher
    ): OkHttpClient {
        return rawOkHttpClient.newBuilder()
            .addInterceptor(oauthTokenRefresher)
            .apply {
                if (BuildConfig.DEBUG) {
                    addNetworkInterceptor(HttpLoggingInterceptor().setLevel(BODY))
                    addNetworkInterceptor(StethoInterceptor())
                }
            }
            .build()
    }

    @Provides
    @Singleton
    internal fun provideNetworkCache(application: TvarkauApplication): Cache {
        return Cache(File(application.cacheDir, "responses"), SIZE_OF_CACHE)
    }

    companion object {
        private const val SIZE_OF_CACHE = 10 * 1024 * 1024L // 10MB
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RawOkHttpClient
