package lt.vilnius.tvarkau

import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.entity.Problem
import rx.Observable

/**
 * @author Martynas Jurkus
 */

val Problem.wrapInResponse: Observable<ApiResponse<Problem>>
    get() {
        val response = ApiResponse<Problem>()
        response.result = this
        return Observable.just(response)
    }