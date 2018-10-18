package lt.vilnius.tvarkau.api

data class ApiValidationError(
    val field: String,
    val value: String
)
