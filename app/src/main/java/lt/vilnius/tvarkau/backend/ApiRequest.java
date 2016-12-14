package lt.vilnius.tvarkau.backend;

import android.support.annotation.VisibleForTesting;

public class ApiRequest<P> {

    private String method;
    private int id;
    private P params;

    public ApiRequest(ApiMethod method, P params) {
        this.method = method.name().toLowerCase();
        this.id = method.getId();
        this.params = params;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public P getParams() {
        return params;
    }
}