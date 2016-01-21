package lt.vilnius.tvarkau.network.service;

import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.entity.UserProfile;
import lt.vilnius.tvarkau.network.APIClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public interface UserService {

    /**
     * Generates a token for user registration.
     */
    @GET("/token")
    Call<Token> getToken();

    /**
     * Creates user with a provided token.
     */
    @POST("/me")
    Call<Response> createUser(@Body Token token);

    /**
     * Shows current user's profile.
     */
    @GET("/me")
    Call<UserProfile> getCurrentUser(@Header(APIClient.X_AUTH) Token token);


    /**
     * Show User's public profile
     */
    @GET("/users/{user_id}")
    Call<UserProfile> getUser(@Header(APIClient.X_AUTH) Token token,
                              @Path("user_id") int userId);




}