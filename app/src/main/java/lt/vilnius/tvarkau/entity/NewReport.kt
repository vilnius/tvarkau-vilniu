package lt.vilnius.tvarkau.entity

import lt.vilnius.tvarkau.mvp.presenters.NewReportData

data class NewReport(
    val reportTypeId: Int,
    val lat: Double,
    val lng: Double,
    val description: String,
    val address: String,
    val reportStatusId: Int = 1 //TODO remove when issue on backend is resolved
) {
    companion object {
        fun from(reportData: NewReportData): NewReport {
            return NewReport(
                reportTypeId = reportData.reportType.id,
                lat = reportData.latitude!!,
                lng = reportData.longitude!!,
                description = reportData.description,
                address = reportData.address
            )
        }
    }
}
