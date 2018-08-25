package lt.vilnius.tvarkau.repository

import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.entity.ReportType
import javax.inject.Inject


class ReportTypeRepository @Inject constructor(
    private val dao: ReportTypeDao,
    private val api: TvarkauMiestaApi
) {

    fun getById(reportTypeId: Int): ReportType {
        val reportType = dao.getById(reportTypeId)

        return if (reportType != null) {
            reportType
        } else {
            api.getReportTypes()
                .map { it.reportTypes }
                .doOnSuccess { dao.insertAll(it) }
                .blockingGet()

            dao.getById(reportTypeId)!!
        }
    }
}
