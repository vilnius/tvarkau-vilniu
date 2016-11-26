package lt.vilnius.tvarkau.network;

import java.io.IOException;

import lt.vilnius.tvarkau.dagger.module.APIModule;
import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.utils.Authentication;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Adapted https://gist.github.com/alex-shpak/da1e65f52dc916716930
 */
public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();

        Token token = Authentication.getToken();

        if (token != null)
            builder.header(APIModule.X_AUTH, token.getToken());

        request = builder.build();

        return chain.proceed(request);
    }

}
