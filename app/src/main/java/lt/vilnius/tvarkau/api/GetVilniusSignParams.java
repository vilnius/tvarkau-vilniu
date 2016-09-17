package lt.vilnius.tvarkau.api;

public class GetVilniusSignParams {

    private String login;
    private String password;

    public GetVilniusSignParams(String email, String password) {
        this.login = email;
        this.password = password;
    }
}
