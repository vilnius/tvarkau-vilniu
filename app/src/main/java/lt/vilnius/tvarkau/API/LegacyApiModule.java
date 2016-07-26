package lt.vilnius.tvarkau.API;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lt.vilnius.tvarkau.network.TokenAuthenticator;
import lt.vilnius.tvarkau.network.TokenInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class LegacyApiModule {

    public static final String API_BASE_URL = "http://www.vilnius.lt/m/m_problems/files/mobile/";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {

        return new OkHttpClient.Builder()
            .authenticator(new TokenAuthenticator())
            .addNetworkInterceptor(new Interceptor() {
                @Override public Response intercept(Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.header("Content-Type", "application/json");
                    return chain.proceed(requestBuilder.build());
                }
            })
            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(new TokenInterceptor())
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {

        Gson gson = new GsonBuilder()
            .setLenient()
            .create();

        return new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(okHttpClient)
            .build();
    }

    @Provides
    @Singleton
    public LegacyApiService provideProblemService(Retrofit retrofit) {
        return retrofit.create(LegacyApiService.class);
    }
}
