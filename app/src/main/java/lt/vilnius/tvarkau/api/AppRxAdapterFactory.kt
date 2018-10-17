package lt.vilnius.tvarkau.api

import com.google.gson.JsonParseException
import io.reactivex.Single
import io.reactivex.functions.Function
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.Type

class AppRxAdapterFactory(
    private val factory: CallAdapter.Factory
) : CallAdapter.Factory() {

    @Suppress("UNCHECKED_CAST")
    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val original = factory.get(returnType, annotations, retrofit) ?: return null
        return CallAdapterDecorator(original as CallAdapter<Any, *>)
    }

    private inner class CallAdapterDecorator(val decorated: CallAdapter<Any, *>) : CallAdapter<Any, Any> {
        override fun responseType() = decorated.responseType()

        override fun adapt(call: Call<Any>): Any {
            val orig = decorated.adapt(call)!!

            return when (orig) {
                is Single<*> -> wrapSingle(orig)
                else -> throw IllegalArgumentException("Unsupported type of call " + orig.toString())
            }
        }

        private fun wrapSingle(orig: Single<*>): Single<BaseResponse> {
            return orig
                .cast(BaseResponse::class.java)
                .onErrorResumeNext(mapHttpExceptionToBaseResponse())
                .flatMap(throwOnApiError())
                .doOnError { processError(it) }
        }

        private fun mapHttpExceptionToBaseResponse(): (Throwable) -> Single<out BaseResponse> {
            return {
                if (it is HttpException) {
                    try {
                        val response = ApiError.extractBaseResponse(it)
                        if (response.isStatusOk) {
                            throw it
                        }
                        Single.just(response)
                    } catch (e: Exception) {
                        Single.error<BaseResponse>(ApiError.of(e))
                    }
                } else {
                    Single.error<BaseResponse>(ApiError(it))
                }
            }
        }

        private fun processError(error: Throwable) {
            val apiError = ApiError.of(error)
            logError(apiError)
        }

        private fun throwOnApiError(): Function<BaseResponse, Single<BaseResponse>> {
            return Function { response ->
                if (response.isStatusOk) {
                    Single.just(response)
                } else {
                    Single.error(ApiError(response))
                }
            }
        }

        private fun logError(error: ApiError) {
            when (error.errorType) {
                ApiError.ErrorType.SERVER -> {
                    val response = error.retrofitResponse!!
                    Timber.e(
                        error,
                        "[${response.raw().request().url()}] " +
                            "Server error: ${response.code()}, " +
                            "Reason: ${response.message()}"
                    )
                }
                ApiError.ErrorType.VALIDATION -> error.validationErrors.forEach { validationError ->
                    Timber.e(error, validationError.toString())
                }
                ApiError.ErrorType.API -> {
                    Timber.e(error, error.message)
                }
                ApiError.ErrorType.SYSTEM -> {
                    if (error.cause is JsonParseException) {
                        Timber.e(error.message, error)
                    }
                }
            }
        }
    }
}
