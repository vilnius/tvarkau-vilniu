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
) : Parcelable