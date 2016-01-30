package lt.vilnius.tvarkau.network;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lt.vilnius.tvarkau.network.service.IssueService;
import lt.vilnius.tvarkau.network.service.MediaService;
import lt.vilnius.tvarkau.network.service.UserService;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
@Module
public class APIModule {

    public static final String X_AUTH = "X-Auth";
    public static final String API_BASE_URL = "http://private-1dd02-tvarkauvilniu.apiary-mock.com/";

    @Provides
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(new TokenInterceptor())
                .build();
    }

    @Provides
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    public UserService provideUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

    @Provides
    public IssueService provideIssueService(Retrofit retrofit) {
        return retrofit.create(IssueService.class);
    }

    @Provides
    public MediaService provideMediaService(Retrofit retrofit) {
        return retrofit.create(MediaService.class);
    }

}

