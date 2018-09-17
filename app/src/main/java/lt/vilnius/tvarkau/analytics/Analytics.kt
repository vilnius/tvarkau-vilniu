package lt.vilnius.tvarkau.analytics

import android.app.Activity
import lt.vilnius.tvarkau.entity.ReportEntity

interface Analytics {

    fun trackOpenFragment(activity: Activity, name: String)

    fun trackCloseFragment(name: String)

    fun trackViewReport(reportEntity: ReportEntity)

    fun trackReportRegistration(reportType: String, photoCount: Int)

    fun trackReportValidation(validationError: String)

    fun trackPersonalDataSharingEnabled(enabled: Boolean)

    fun trackLogIn()

    fun trackGooglePlayServicesError(resultCode: Int)

    fun trackApplyReportFilter(status: String, category: String, target: String)

    fun flush()

}
