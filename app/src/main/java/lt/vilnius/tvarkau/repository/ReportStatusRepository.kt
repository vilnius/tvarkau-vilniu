package lt.vilnius.tvarkau.repository

import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.entity.ReportStatus
import javax.inject.Inject


class ReportStatusRepository @Inject constructor(
    private val dao: ReportStatusDao,
    private val api: TvarkauMiestaApi
) {

    fun getAll(): List<ReportStatus> {
        return dao.getAll()
    }

    fun getById(reportStatusId: Int): ReportStatus {
        val reportStatus = dao.getById(reportStatusId)

        return if (reportStatus != null) {
            reportStatus
        } else {
            val statuses = api.getReportStatuses()
                .map { it.reportStatuses }
                .blockingGet()

            dao.insertAll(statuses)
            dao.getById(reportStatusId)!!
        }
    }
}
