package lt.vilnius.tvarkau.mvp.interactors

import rx.Single

/**
 * @author Martynas Jurkus
 */
interface ReportTypesInteractor {

    fun getReportTypes(): Single<List<String>>
}