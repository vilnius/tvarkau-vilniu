package lt.vilnius.tvarkau.utils

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsUtil {

    private lateinit var appContext: Context

    private val analytics: FirebaseAnalytics by lazy { FirebaseAnalytics.getInstance(appContext) }

    fun init(context: Context) {
        this.appContext = context.applicationContext
    }

    fun trackCurrentFragment(activity: Activity, fragment: Fragment) {
        analytics.setCurrentScreen(activity, fragment.javaClass.simpleName, null)
    }


}