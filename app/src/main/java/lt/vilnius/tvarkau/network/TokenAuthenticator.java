package lt.vilnius.tvarkau.network;

import java.io.IOException;

import javax.inject.Inject;

import dagger.Lazy;
import lt.vilnius.tvarkau.dagger.module.APIModule;
import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.utils.Authentication;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Adapted https://github.com/square/okhttp/wiki/Recipes#handling-authentication
 */
public class TokenAuthenticator implements Authenticator {

    protected static final int MAX_RETRIES = 3;

    @Inject Lazy<UserService> userService;

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // Give up after unsuccessful retries
        if (responseCount(response) >= MAX_RETRIES) {
            return null;
        }

        Token newAccessToken = userService.get().getToken().execute().body();

        Authentication.setToken(newAccessToken);

        return response.request().newBuilder()
                .header(APIModule.X_AUTH, newAccessToken.getToken())
                .build();
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
