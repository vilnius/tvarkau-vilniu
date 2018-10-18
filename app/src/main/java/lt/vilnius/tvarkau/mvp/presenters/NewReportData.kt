package lt.vilnius.tvarkau.mvp.presenters

import lt.vilnius.tvarkau.entity.ReportType
import java.io.File

data class NewReportData(
    val reportType: ReportType,
    val description: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val dateTime: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val name: String? = null,
    val personalCode: String? = null,
    val photoUrls: List<File> = emptyList(),
    val licencePlate: String? = null
)
