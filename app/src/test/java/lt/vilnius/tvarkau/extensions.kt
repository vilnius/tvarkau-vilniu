package lt.vilnius.tvarkau

import io.reactivex.Single
import lt.vilnius.tvarkau.backend.ApiResponse

/**
 * @author Martynas Jurkus
 */

val <T> T.wrapInResponse: Single<ApiResponse<T>>
    get() {
        val response = ApiResponse<T>()
        response.result = this
        return Single.just(response)
    }