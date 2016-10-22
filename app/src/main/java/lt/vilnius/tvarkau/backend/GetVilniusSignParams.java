package lt.vilnius.tvarkau.backend;

public class GetVilniusSignParams {

    private String login;
    private String password;

    public GetVilniusSignParams(String email, String password) {
        this.login = email;
        this.password = password;
    }
}
