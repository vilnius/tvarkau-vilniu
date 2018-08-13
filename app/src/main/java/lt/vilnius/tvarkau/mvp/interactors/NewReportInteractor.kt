package lt.vilnius.tvarkau.mvp.interactors

import io.reactivex.Single
import lt.vilnius.tvarkau.mvp.presenters.NewReportData

/**
 * @author Martynas Jurkus
 */
interface NewReportInteractor {

    fun submitReport(data: NewReportData): Single<String>
}