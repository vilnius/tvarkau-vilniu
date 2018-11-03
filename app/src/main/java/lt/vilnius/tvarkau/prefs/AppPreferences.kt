package lt.vilnius.tvarkau.prefs

import com.vinted.preferx.IntPreference
import com.vinted.preferx.LongPreference
import com.vinted.preferx.ObjectPreference
import lt.vilnius.tvarkau.auth.ApiToken
import lt.vilnius.tvarkau.entity.City


interface AppPreferences {

    val apiToken: ObjectPreference<ApiToken>

    val photoInstructionsLastSeen: LongPreference

    val reportStatusSelectedFilter: IntPreference

    val reportTypeSelectedFilter: IntPreference

    val reportStatusSelectedListFilter: IntPreference

    val reportTypeSelectedListFilter: IntPreference

    val selectedCity: ObjectPreference<City>
}
