package lt.vilnius.tvarkau.API;

import android.support.annotation.Nullable;

public class ApiResponse<R> {

    int id;
    R result;
    @Nullable ApiError error;

    public R getResult() {
        return result;
    }
}
