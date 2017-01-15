package lt.vilnius.tvarkau.fragments.interactors

import android.content.SharedPreferences
import rx.Single

/**
 * @author Martynas Jurkus
 */
class SharedPreferencesMyReportsInteractor(
        private val myProblemsPreferences: SharedPreferences
) : MyReportsInteractor {

    override fun getReportIds(): Single<List<String>> {
        return Single.create<List<String>> {
            it.onSuccess(getReportIdsImmediate())
        }
    }

    override fun getReportIdsImmediate(): List<String> {
        return myProblemsPreferences.all.keys.map {
            myProblemsPreferences.getString(it, "")
        }
    }

    override fun saveReportId(reportId: String) {
        myProblemsPreferences
                .edit()
                .putString("$PROBLEM_PREFERENCE_KEY$reportId", reportId)
                .apply()
    }

    override fun removeReportId(reportId: String) {
        myProblemsPreferences.edit().remove("$PROBLEM_PREFERENCE_KEY$reportId").apply()
    }

    companion object {
        const val PROBLEM_PREFERENCE_KEY = "problem"
    }
}