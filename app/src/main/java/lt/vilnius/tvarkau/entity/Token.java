package lt.vilnius.tvarkau.entity;

public class Token {

    protected String token;

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return getToken();
    }
}
