package lt.vilnius.tvarkau.prefs

import lt.vilnius.tvarkau.auth.ApiToken


interface AppPreferences {

    val apiToken: ObjectPreference<ApiToken>

    val photoInstructionsLastSeen: LongPreference

    val reportStatusSelectedFilter: StringPreference

    val reportTypeSelectedFilter: StringPreference

    val reportStatusSelectedListFilter: StringPreference

    val reportTypeSelectedListFilter: StringPreference
}