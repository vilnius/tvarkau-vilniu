package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences


class AppPreferencesImpl(
        private val preferences: SharedPreferences
) : AppPreferences {

    override val photoInstructionsLastSeen by lazy {
        LongPreferenceImpl(preferences, LAST_DISPLAYED_PHOTO_INSTRUCTIONS, 0L)
    }
    override val reportStatusSelectedFilter by lazy {
        StringPreferenceImpl(preferences, SELECTED_FILTER_REPORT_STATUS, "")
    }

    override val reportTypeSelectedFilter by lazy {
        StringPreferenceImpl(preferences, SELECTED_FILTER_REPORT_TYPE, "")
    }

    override val reportStatusSelectedListFilter by lazy {
        StringPreferenceImpl(preferences, LIST_SELECTED_FILTER_REPORT_STATUS, "")
    }

    override val reportTypeSelectedListFilter by lazy {
        StringPreferenceImpl(preferences, LIST_SELECTED_FILTER_REPORT_TYPE, "")
    }

    companion object {
        const val COMMON_PREFERENCES = "TVARKAU-VILNIU_PREFS"
        const val MY_PROBLEMS_PREFERENCES = "my_problem_preferences"
        const val SELECTED_FILTER_REPORT_STATUS = "filter_report_status"
        const val SELECTED_FILTER_REPORT_TYPE = "filter_report_type"
        const val LIST_SELECTED_FILTER_REPORT_STATUS = "list_filter_report_status"
        const val LIST_SELECTED_FILTER_REPORT_TYPE = "list_filter_report_type"
        const val LAST_DISPLAYED_PHOTO_INSTRUCTIONS = "last_displayed_photo_instructions"
    }
}