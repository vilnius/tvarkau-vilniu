package lt.vilnius.tvarkau.network;


import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.network.service.IssueService;
import lt.vilnius.tvarkau.network.service.MediaService;
import lt.vilnius.tvarkau.network.service.UserService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public class APIClient {

    public static final String X_AUTH = "X-Auth";
    public static final String API_BASE_URL = "http://private-1dd02-tvarkauvilniu.apiary-mock.com/";

    private static APIClient apiClient;

    public static APIClient getInstance() {
        if (apiClient == null)
            apiClient = new APIClient();

        return apiClient;
    }

    protected Retrofit retrofit;
    protected OkHttpClient okHttpClient;

    protected UserService userService;
    protected IssueService issueService;
    protected MediaService mediaService;

    private APIClient() {
        okHttpClient = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator())
                .addInterceptor(new TokenInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        userService = retrofit.create(UserService.class);
        issueService = retrofit.create(IssueService.class);
        mediaService = retrofit.create(MediaService.class);
    }

    public Call<Problem> getIssue(int issueId) {
        return issueService.getIssue(issueId);
    }

    public Call<Token> getToken() {
        return userService.getToken();
    }

    // TODO add missing endpoint methods

}

