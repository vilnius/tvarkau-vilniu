package lt.vilnius.tvarkau.auth

data class ApiToken(
        val expiresIn: Long = 0,
        val tokenType: String = "",
        val accessToken: String = "",
        val scope: String = "",
        val createdAt: Long = 0
)
