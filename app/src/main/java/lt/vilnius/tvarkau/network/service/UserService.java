package lt.vilnius.tvarkau.network.service;

import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.entity.UserProfile;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public interface UserService {

    /**
     * Generates a token for user registration.
     */
    @GET("tokens")
    Call<Token> getToken();

    /**
     * Creates user with a provided token.
     */
    @POST("me")
    Call<Response> createUser(@Body Token token);

    /**
     * Shows current user's profile.
     */
    @GET("me")
    Call<UserProfile> getCurrentUser();


    /**
     * Show User's public profile
     */
    @GET("users/{user_id}")
    Call<UserProfile> getUser(@Path("user_id") int userId);


}