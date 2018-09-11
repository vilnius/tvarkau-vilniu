package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences
import com.vinted.preferx.PreferxSerializer
import com.vinted.preferx.longPreference
import com.vinted.preferx.objectPreference
import com.vinted.preferx.stringPreference
import lt.vilnius.tvarkau.auth.ApiToken
import lt.vilnius.tvarkau.data.GsonSerializer
import lt.vilnius.tvarkau.entity.City
import java.lang.reflect.Type


class AppPreferencesImpl(
    private val preferences: SharedPreferences,
    private val gsonSerializer: GsonSerializer
) : AppPreferences {

    private val serializer = object : PreferxSerializer {
        override fun fromString(string: String, type: Type): Any {
            return gsonSerializer.fromJsonType(string, type)
        }

        override fun toString(value: Any): String {
            return gsonSerializer.toJson(value)
        }
    }

    override val apiToken by lazy {
        preferences.objectPreference(
            name = API_TOKEN,
            defaultValue = ApiToken(),
            serializer = serializer,
            clazz = ApiToken::class.java
        )
    }

    override val photoInstructionsLastSeen by lazy {
        preferences.longPreference(LAST_DISPLAYED_PHOTO_INSTRUCTIONS, 0L)
    }
    override val reportStatusSelectedFilter by lazy {
        preferences.stringPreference(SELECTED_FILTER_REPORT_STATUS, "")
    }

    override val reportTypeSelectedFilter by lazy {
        preferences.stringPreference(SELECTED_FILTER_REPORT_TYPE, "")
    }

    override val reportStatusSelectedListFilter by lazy {
        preferences.stringPreference(LIST_SELECTED_FILTER_REPORT_STATUS, "")
    }

    override val reportTypeSelectedListFilter by lazy {
        preferences.stringPreference(LIST_SELECTED_FILTER_REPORT_TYPE, "")
    }

    override val selectedCity by lazy {
        preferences.objectPreference(
            name = SELECTED_CITY,
            defaultValue = City.NOT_SELECTED,
            serializer = serializer,
            clazz = City::class.java
        )
    }

    companion object {
        const val API_TOKEN = "api_token"
        const val SELECTED_CITY = "selected_city"
        const val SELECTED_FILTER_REPORT_STATUS = "filter_report_status"
        const val SELECTED_FILTER_REPORT_TYPE = "filter_report_type"
        const val LIST_SELECTED_FILTER_REPORT_STATUS = "list_filter_report_status"
        const val LIST_SELECTED_FILTER_REPORT_TYPE = "list_filter_report_type"
        const val LAST_DISPLAYED_PHOTO_INSTRUCTIONS = "last_displayed_photo_instructions"
    }
}
