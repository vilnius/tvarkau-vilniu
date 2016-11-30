package lt.vilnius.tvarkau.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import lt.vilnius.tvarkau.entity.Problem

class RemoteAnalytics(appContext: Context) : Analytics {

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(appContext)
    }

    private val PARAM_PROBLEM_STATUS = "problem_status"

    override fun trackCurrentFragment(activity: Activity, fragment: Fragment) {
        analytics.setCurrentScreen(activity, fragment.javaClass.simpleName, null)
    }

    override fun trackViewProblem(problem: Problem) = Bundle().run {
        putString(FirebaseAnalytics.Param.ITEM_ID, problem.problemId)
        putString(FirebaseAnalytics.Param.ITEM_NAME, problem.id)
        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, problem.getType())
        putString(PARAM_PROBLEM_STATUS, problem.status)

        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, this)
    }

}