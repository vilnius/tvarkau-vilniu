package lt.vilnius.tvarkau.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


data class Profile(
        @SerializedName("Profile_Name")
        var name: String? = null,
        @SerializedName("Profile_Email")
        var email: String? = null,
        @SerializedName("Profile_Mobile_Phone")
        var mobilePhone: String? = null,
        @SerializedName("personal_code")
        var personalCode: String? = null
) {

    @Deprecated("Use GsonSerializer")
    fun createJsonData(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    override fun toString(): String {
        return createJsonData()
    }
}
