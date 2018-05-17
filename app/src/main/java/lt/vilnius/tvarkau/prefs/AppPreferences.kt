package lt.vilnius.tvarkau.prefs

import com.vinted.preferx.LongPreference
import com.vinted.preferx.ObjectPreference
import com.vinted.preferx.StringPreference
import lt.vilnius.tvarkau.auth.ApiToken


interface AppPreferences {

    val apiToken: ObjectPreference<ApiToken>

    val photoInstructionsLastSeen: LongPreference

    val reportStatusSelectedFilter: StringPreference

    val reportTypeSelectedFilter: StringPreference

    val reportStatusSelectedListFilter: StringPreference

    val reportTypeSelectedListFilter: StringPreference
}