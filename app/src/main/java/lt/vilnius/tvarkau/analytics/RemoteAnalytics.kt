package lt.vilnius.tvarkau.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.mixpanel.android.mpmetrics.MixpanelAPI
import lt.vilnius.tvarkau.entity.Problem

class RemoteAnalytics(appContext: Context, private val mixpanelAPI: MixpanelAPI) : Analytics {

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(appContext)
    }


    override fun trackCurrentFragment(activity: Activity, fragment: Fragment) {
        val fragmentName = fragment.javaClass.simpleName

        Bundle().run {
            putString(PARAM_FRAGMENT_ACTIVITY, activity.javaClass.simpleName)

            logEvent("open_$fragmentName", this)
        }

        analytics.setCurrentScreen(activity, fragmentName, null)
    }

    override fun trackViewProblem(problem: Problem) = Bundle().run {
        putString(FirebaseAnalytics.Param.ITEM_ID, problem.problemId)
        putString(FirebaseAnalytics.Param.ITEM_NAME, problem.id)
        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, problem.getType())
        putString(PARAM_PROBLEM_STATUS, problem.status)

        logEvent(FirebaseAnalytics.Event.VIEW_ITEM, this)
    }

    override fun trackReportRegistration(reportType: String, photoCount: Int) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, reportType)
            putInt(PARAM_PHOTO_COUNT, photoCount)
        }

        logEvent(EVENT_NEW_REPORT, params)
    }

    override fun trackReportValidation(validationError: String) {
        val params = Bundle().apply {
            putString(PARAM_VALIDATION_MESSAGE, validationError)
        }

        logEvent(EVENT_VALIDATION_ERROR, params)
    }

    override fun trackPersonalDataSharingEnabled(enabled: Boolean) {
        val params = Bundle().apply {
            putBoolean(PARAM_ENABLED, enabled)
        }

        logEvent(EVENT_SHARE_PERSONAL_DATA, params)
    }

    override fun trackLogIn() =
        logEvent(FirebaseAnalytics.Event.LOGIN, Bundle.EMPTY)

    private fun logEvent(name: String, bundle: Bundle) {
        analytics.logEvent(name, bundle)

        val properties = bundle.keySet().map { key -> key to bundle[key] }.toMap()
        mixpanelAPI.trackMap(name, properties)
    }

    companion object {
        private const val PARAM_PROBLEM_STATUS = "problem_status"
        private const val PARAM_FRAGMENT_ACTIVITY = "activity"
        private const val PARAM_PHOTO_COUNT = "photo_count"
        private const val PARAM_VALIDATION_MESSAGE = "validation_message"
        private const val PARAM_ENABLED = "enabled"

        private const val EVENT_NEW_REPORT = "new_report"
        private const val EVENT_VALIDATION_ERROR = "validation_error"
        private const val EVENT_SHARE_PERSONAL_DATA = "personal_data"
    }
}