package lt.vilnius.tvarkau.prefs

import com.vinted.preferx.LongPreference
import com.vinted.preferx.ObjectPreference
import com.vinted.preferx.StringPreference
import lt.vilnius.tvarkau.auth.ApiToken
import lt.vilnius.tvarkau.entity.City


interface AppPreferences {

    val apiToken: ObjectPreference<ApiToken>

    val photoInstructionsLastSeen: LongPreference

    val reportStatusSelectedFilter: StringPreference

    val reportTypeSelectedFilter: StringPreference

    val reportStatusSelectedListFilter: StringPreference

    val reportTypeSelectedListFilter: StringPreference

    val selectedCity: ObjectPreference<City>
}
