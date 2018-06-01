package lt.vilnius.tvarkau.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "reports"
)
data class Report(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "ref_no")
    val refNo: String? = null,
    @ColumnInfo(name = "report_type_id")
    val reportTypeId: Int,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lng")
    val lng: Double,
    @ColumnInfo(name = "status_status_id")
    val reportStatusId: Int,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "answer")
    val answer: String? = null,
    @ColumnInfo(name = "license_plate_no")
    val licensePlateNo: String? = null,
    @ColumnInfo(name = "registered_at")
    val registeredAt: String? = null,
    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null
)
