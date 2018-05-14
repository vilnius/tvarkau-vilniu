package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import ca.mimic.oauth2library.OAuth2Client
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.api.ApiEndpoint
import javax.inject.Named
import javax.inject.Qualifier

@Module
class RestAdapterModule {

    @Provides
    @Named(HOST)
    fun provideHost(): String {
        return "https://api.tvarkaumiesta.lt"
    }

    @Provides
    @ApiOAuth
    fun provideOAuthEndpoint(
            application: Application,
            @Named(HOST) host: String
    ): ApiEndpoint {
        return ApiEndpoint(application.getString(R.string.api_root_oauth).format(host))
    }

    @Provides
    @GuestToken
    fun provideGuestTokenOAuthBuilder(
            @ApiOAuth endpoint: ApiEndpoint,
            @RawOkHttpClient client: okhttp3.OkHttpClient
    ): OAuth2Client.Builder {
        return OAuth2Client.Builder(
                OAUTH_CLIENT_ID,
                "",
                endpoint.url + OAUTH_TOKEN_ENDPOINT
        )
                .grantType("password")
                .username("guest")
                .password("guest")
                .scope("user")
                .okHttpClient(client)
    }

    companion object {
        const val HOST = "host"
        const val OAUTH_CLIENT_ID = "android"
        const val OAUTH_TOKEN_ENDPOINT = "token"
    }
}

@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ApiOAuth

@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class GuestToken
