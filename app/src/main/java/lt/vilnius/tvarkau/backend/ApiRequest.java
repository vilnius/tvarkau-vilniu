package lt.vilnius.tvarkau.backend;

public class ApiRequest<P> {

    private String method;
    private int id;
    private P params;

    public ApiRequest(ApiMethod method, P params) {
        this.method = method.name().toLowerCase();
        this.id = method.getId();
        this.params = params;
    }
}