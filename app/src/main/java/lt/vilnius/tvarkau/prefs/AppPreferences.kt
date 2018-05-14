package lt.vilnius.tvarkau.prefs


interface AppPreferences {

    val photoInstructionsLastSeen: LongPreference

    val reportStatusSelectedFilter: StringPreference

    val reportTypeSelectedFilter: StringPreference

    val reportStatusSelectedListFilter: StringPreference

    val reportTypeSelectedListFilter: StringPreference
}