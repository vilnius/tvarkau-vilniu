package lt.vilnius.tvarkau.analytics

import android.app.Activity
import lt.vilnius.tvarkau.entity.Problem

/**
 * @author Martynas Jurkus
 */
interface Analytics {

    fun trackOpenFragment(activity: Activity, name: String)

    fun trackCloseFragment(name: String)

    fun trackViewProblem(problem: Problem)

    fun trackReportRegistration(reportType: String, photoCount: Int)

    fun trackReportValidation(validationError: String)

    fun trackPersonalDataSharingEnabled(enabled: Boolean)

    fun trackLogIn()

    fun trackGooglePlayServicesError(resultCode: Int)

    fun trackApplyReportFilter(status: String, category: String, target: String)

}