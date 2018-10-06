package lt.vilnius.tvarkau.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User(
    val id: Int,
    val email: String?,
    val createdAt: String
) : Parcelable
