package lt.vilnius.tvarkau.network;

import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.entity.UserProfile;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    /**
     * Generates a token for user registration.
     */
    @GET("tokens")
    Call<Token> getToken();
}