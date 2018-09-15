package lt.vilnius.tvarkau.entity

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportEntity(
    val id: Int,
    val refNo: String? = null,
    val reportType: ReportType,
    val lat: Double,
    val lng: Double,
    val userId: Int,
    val reportStatus: ReportStatus,
    val description: String,
    val answer: String? = null,
    val licensePlateNo: String? = null,
    val registeredAt: String? = null,
    val completedAt: String? = null,
    val photos: List<String> = emptyList()
) : Parcelable {

    val latLng: LatLng
        get() = LatLng(lat, lng)
}
