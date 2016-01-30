package lt.vilnius.tvarkau.utils;

import lt.vilnius.tvarkau.entity.Token;

/**
 * Created by Karolis Vycius on 2016-01-30.
 */
public class Authentication {

    protected static Token token;

    public static Token getToken() {
        return token;
    }

    public static void setToken(Token token) {
        Authentication.token = token;
    }

}
