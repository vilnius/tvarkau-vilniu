package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences

interface Serializer<V> {
    fun serialize(storage: SharedPreferences.Editor, key: String, value: V)
    fun unserialize(source: SharedPreferences, key: String, default: V): V
}
