package lt.vilnius.tvarkau

import lt.vilnius.tvarkau.backend.ApiResponse
import rx.Observable
import rx.Single

/**
 * @author Martynas Jurkus
 */

val <T> T.wrapInResponse: Observable<ApiResponse<T>>
    get() {
        val response = ApiResponse<T>()
        response.result = this
        return Observable.just(response)
    }

val <T> T.wrapInSingleResponse: Single<ApiResponse<T>>
    get() {
        return this.wrapInResponse.toSingle()
    }