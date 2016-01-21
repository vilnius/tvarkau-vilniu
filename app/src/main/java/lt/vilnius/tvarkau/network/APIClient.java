package lt.vilnius.tvarkau.network;

import lt.vilnius.tvarkau.network.service.IssueService;
import lt.vilnius.tvarkau.network.service.MediaService;
import lt.vilnius.tvarkau.network.service.UserService;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public class APIClient {
    public static final String X_AUTH = "X-Auth";


    public static final String API_BASE_URL = "http://private-1dd02-tvarkauvilniu.apiary-mock.com";

    protected static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    protected static UserService userService = retrofit.create(UserService.class);
    protected static IssueService issueService = retrofit.create(IssueService.class);
    protected static MediaService mediaService = retrofit.create(MediaService.class);

}
