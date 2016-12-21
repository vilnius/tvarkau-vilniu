package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences
import timber.log.Timber

abstract class BasePreferenceImpl<T> protected constructor(
        private val preferences: SharedPreferences,
        val key: String,
        private val defaultValue: T,
        private val serializer: Serializer<T>
) : BasePreference<T> {

    override fun set(value: T) {
        preferences.edit().apply() {
            serializer.serialize(this, key, value)
        }.apply()
    }

    override fun setSync(value: T) {
        preferences.edit().apply() {
            serializer.serialize(this, key, value)
        }.commit()
    }

    override fun get(): T {
        try {
            return serializer.unserialize(preferences, key, defaultValue)
        } catch (e: ClassCastException) {
            Timber.e(e)
        }

        return defaultValue
    }

    override fun delete() {
        preferences.edit().remove(key).apply()
    }

    override fun isSet() = preferences.contains(key)
}