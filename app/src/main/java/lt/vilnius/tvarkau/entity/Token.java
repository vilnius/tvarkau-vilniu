package lt.vilnius.tvarkau.entity;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
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
