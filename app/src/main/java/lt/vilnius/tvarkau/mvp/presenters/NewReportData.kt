package lt.vilnius.tvarkau.mvp.presenters

import java.io.File

/**
 * @author Martynas Jurkus
 */
data class NewReportData(
        val description: String = "",
        val reportType: String = "",
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