package lt.vilnius.tvarkau.network;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class APIModule {

    public static final String X_AUTH = "X-Auth";
    public static final String API_BASE_URL = "http://private-1dd02-tvarkauvilniu.apiary-mock.com/";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(new TokenInterceptor())
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public UserService provideUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }
}

