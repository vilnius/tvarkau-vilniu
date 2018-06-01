package lt.vilnius.tvarkau.repository

import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.entity.ReportStatus
import javax.inject.Inject


class ReportStatusRepository @Inject constructor(
    private val dao: ReportStatusDao,
    private val api: TvarkauMiestaApi
) {

    fun getById(reportStatusId: Int): ReportStatus {
        val reportStatus = dao.getById(reportStatusId)

        return if (reportStatus != null) {
            reportStatus
        } else {
            api.getReportStatuses()
                .map { it.reportStatuses }
                .doOnSuccess { dao.insertAll(it) }
                .blockingGet()

            dao.getById(reportStatusId)!!
        }
    }
}
