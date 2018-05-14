package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences
import lt.vilnius.tvarkau.data.GsonSerializer
import java.lang.ref.SoftReference

class GsonPrefSerializer<T>(private val gson: GsonSerializer, val clazz: Class<T>) : Serializer<T> {
    private var cached: SoftReference<Cache<T>>? = null

    override fun serialize(storage: SharedPreferences.Editor, key: String, value: T) {
        val json = gson.toJson(value)
        cached = SoftReference(Cache(value, json))
        storage.putString(key, json)
    }

    override fun unserialize(source: SharedPreferences, key: String, default: T): T {
        val json = source.getString(key, "") ?: ""

        val cachedValue = cached?.get()
        if (cachedValue != null && cachedValue.json == json) {
            return cachedValue.value ?: default
        } else {
            val value: T = gson.fromJsonType<T>(json, clazz)
            cached = SoftReference(Cache(value, json))
            return value ?: default
        }
    }

    private class Cache<T>(val value: T, val json: String)
}
