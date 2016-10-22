package lt.vilnius.tvarkau.utils;

import lt.vilnius.tvarkau.entity.Token;

public class Authentication {

    protected static Token token;

    private Authentication() {}

    public static Token getToken() {
        return token;
    }

    public static void setToken(Token token) {
        Authentication.token = token;
    }

}
