package lt.vilnius.tvarkau.mvp.interactors

import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import rx.Single

/**
 * @author Martynas Jurkus
 */
interface NewReportInteractor {

    fun submitReport(data: NewReportData): Single<String>
}