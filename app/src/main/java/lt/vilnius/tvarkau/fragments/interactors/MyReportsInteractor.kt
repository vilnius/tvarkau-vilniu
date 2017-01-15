package lt.vilnius.tvarkau.fragments.interactors

import rx.Single

/**
 * @author Martynas Jurkus
 */
interface MyReportsInteractor {

    fun getReportIds(): Single<List<String>>

    fun getReportIdsImmediate(): List<String>

    fun saveReportId(reportId: String)

    fun removeReportId(reportId: String)
}