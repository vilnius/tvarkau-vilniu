package lt.vilnius.tvarkau.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val id: Int,
    val code: String,
    val name: String,
    val lat: Double,
    val lng: Double
) : Parcelable {

    companion object {
        val NOT_SELECTED = City(
            id = -1,
            code = "",
            name = "",
            lat = 0.0,
            lng = 0.0
        )
    }
}
