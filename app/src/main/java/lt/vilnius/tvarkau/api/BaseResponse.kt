package lt.vilnius.tvarkau.api

open class BaseResponse(
    val code: Int = REPOSE_CODE_OK,
    val message: String? = null,
    val errors: List<ApiValidationError> = emptyList()
) {

    val isStatusOk: Boolean
        get() = code == REPOSE_CODE_OK

    val isValidationError: Boolean
        get() = code == REPOSE_CODE_VALIDATION_ERROR

    companion object {
        const val REPOSE_CODE_OK = 0
        const val REPOSE_CODE_VALIDATION_ERROR = 10
    }
}

