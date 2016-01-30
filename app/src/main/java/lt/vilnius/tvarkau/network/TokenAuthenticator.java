package lt.vilnius.tvarkau.network;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import dagger.Lazy;
import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.network.service.UserService;
import lt.vilnius.tvarkau.utils.Authentication;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Karolis Vycius on 2016-01-30.
 * Adapted https://github.com/square/okhttp/wiki/Recipes#handling-authentication
 */
@AutoComponent(modules = APIModule.class)
@AutoInjector
@Singleton
public class TokenAuthenticator implements Authenticator {

    protected static final int maxRetries = 3;

    @Inject
    Lazy<UserService> userService;

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // Give up after unsuccessful retries
        if (responseCount(response) >= maxRetries) {
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
