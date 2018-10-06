package lt.vilnius.tvarkau.entity

data class SocialNetworkUser internal constructor(
    val token: String,
    val email: String,
    val provider: String
) {

    companion object {
        fun forGoogle(token: String, email: String): SocialNetworkUser {
            return SocialNetworkUser(token, email, "google")
        }
    }
}


