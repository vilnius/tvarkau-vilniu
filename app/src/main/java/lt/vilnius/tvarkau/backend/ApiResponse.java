package lt.vilnius.tvarkau.backend;

import android.support.annotation.Nullable;

public class ApiResponse<R> {

    int id;
    R result;
    @Nullable
    Integer error;

    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }
}
