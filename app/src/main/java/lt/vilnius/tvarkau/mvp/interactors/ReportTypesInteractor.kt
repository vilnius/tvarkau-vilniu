package lt.vilnius.tvarkau.mvp.interactors

import io.reactivex.Single

/**
 * @author Martynas Jurkus
 */
interface ReportTypesInteractor {

    fun getReportTypes(): Single<List<String>>
}