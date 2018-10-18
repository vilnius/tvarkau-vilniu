package lt.vilnius.tvarkau.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset

class ApiError : RuntimeException {

    val errorType: ErrorType
    val retrofitResponse: Response<*>?

    val validationErrors: List<ApiValidationError>

    var responseCode: Int = BaseResponse.REPOSE_CODE_OK
        private set

    var response: BaseResponse? = null
        private set

    val httpStatusCode: Int

    private var apiMessage: String? = null

    constructor(cause: Throwable) : super(cause) {
        errorType = ErrorType.SYSTEM
        retrofitResponse = null
        validationErrors = emptyList()
        httpStatusCode = 500
    }

    constructor(httpException: HttpException) : super(httpException) {
        errorType = ErrorType.SERVER
        retrofitResponse = httpException.response()
        validationErrors = emptyList()
        parseApiResponse(httpException)
        httpStatusCode = httpException.code()
    }

    constructor(response: BaseResponse) : super(response.message) {
        this.response = response

        validationErrors = response.errors
        responseCode = response.code
        apiMessage = response.message
        retrofitResponse = null

        errorType = when {
            response.isValidationError -> ErrorType.VALIDATION
            else -> ErrorType.API
        }
        this.httpStatusCode = 400
    }

    private fun parseApiResponse(httpException: HttpException) {
        try {
            response = extractBaseResponse(httpException)

            responseCode = response!!.code
            apiMessage = response!!.message
        } catch (ignored: RuntimeException) {
            val url = httpException.response().raw().request().url()
            Timber.w("Failed parse response from url: $url")
        }
    }

    val firstErrorMessage: String?
        get() = validationErrors.firstOrNull()?.value ?: apiErrorMessage

    val firstValidationErrorField: String
        get() = validationErrors.firstOrNull()?.field ?: ""

    /**
     * @see ErrorType.VALIDATION
     */
    val isValidationError: Boolean
        get() = errorType == ErrorType.VALIDATION

    /**
     * @see ErrorType.API
     */
    val isApiError: Boolean
        get() = errorType == ErrorType.API

    /**
     * @see ErrorType.SERVER
     */
    val isServerError: Boolean
        get() = errorType == ErrorType.SERVER

    /**
     * @see ErrorType.SYSTEM
     */
    val isSystemError: Boolean
        get() = errorType == ErrorType.SYSTEM

    val apiErrorMessage: String?
        get() = apiMessage

    enum class ErrorType {
        /**
         * Unexpected IO errors
         */
        SYSTEM,
        /**
         * Unexpected server errors without response body (basically 5xx)
         */
        SERVER,
        /**
         * Api errors (2xx - 4xx) with error body
         */
        API,
        /**
         * Special case of validation Api error
         */
        VALIDATION,
    }

    companion object {
        fun extractBaseResponse(httpException: HttpException): BaseResponse {
            val body = httpException.response().errorBody() ?: throw IOException("No body")
            val source = body.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer()
            val charset = body.contentType()?.charset(UTF_8) ?: UTF_8

            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            return gson.fromJson(buffer.clone().readString(charset), BaseResponse::class.java)
        }

        fun of(error: Throwable): ApiError {
            return when (error) {
                is ApiError -> error
                is HttpException -> ApiError(error)
                else -> ApiError(error)
            }
        }

        val UTF_8: Charset = Charset.forName("UTF-8")
    }
}
