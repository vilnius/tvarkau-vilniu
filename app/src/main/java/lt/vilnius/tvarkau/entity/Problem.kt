package lt.vilnius.tvarkau.entity

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel
import org.threeten.bp.LocalDateTime

@Parcel
data class Problem(
        @SerializedName("docNo")
        val id: String? = null,
        val problemId: String? = null,
        private val typeName: String? = null,
        private val type: String? = null,
        val description: String? = null,
        val address: String? = null,
        val status: String? = null,
        val answer: String? = null,
        private val entryDate: LocalDateTime? = null,
        private val reportDate: LocalDateTime? = null,
        private val photo: List<String>? = null,
        private val thumbnail: String? = null,
        val completeDate: String? = null,
        @SerializedName("x")
        private val lng: Double = 0.toDouble(),
        @SerializedName("y")
        private val lat: Double = 0.toDouble()
) : Comparable<Problem> {
    fun getType(): String? {
        return typeName ?: type
    }

    fun getEntryDate(): LocalDateTime? {
        return entryDate ?: reportDate
    }

    val latLng: LatLng
        get() = LatLng(lat, lng)

    val photos: List<String>?
        get() {
            return photo ?: thumbnail?.let { listOf(it) }
        }

    override fun compareTo(other: Problem): Int {
        return getEntryDate()?.compareTo(other.getEntryDate()) ?: 0
    }

    companion object {
        const val STATUS_DONE = "Atlikta"
        const val STATUS_RESOLVED = "Išnagrinėta"
        const val STATUS_TRANSFERRED = "Perduota"
        const val STATUS_REGISTERED = "Registruota"
        const val STATUS_POSTPONED = "Atidėta"
    }
}
