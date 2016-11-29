package lt.vilnius.tvarkau.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import lt.vilnius.tvarkau.entity.Problem

class Analytics(appContext: Context) {

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(appContext)
    }

    private val PARAM_PROBLEM_STATUS = "problem_status"

    fun trackCurrentFragment(activity: Activity, fragment: Fragment) {
        analytics.setCurrentScreen(activity, fragment.javaClass.simpleName, null)
    }

    fun trackViewProblem(problem: Problem) = Bundle().run {
        putString(FirebaseAnalytics.Param.ITEM_ID, problem.getReportId())
        putString(FirebaseAnalytics.Param.ITEM_NAME, problem.getId())
        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, problem.type)
        putString(PARAM_PROBLEM_STATUS, problem.getStatus())

        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, this)
    }

}