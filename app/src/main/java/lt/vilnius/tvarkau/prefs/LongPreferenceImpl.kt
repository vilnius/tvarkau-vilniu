package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences

/**
 * @author Martynas Jurkus
 */
open class LongPreferenceImpl(
        preferences: SharedPreferences,
        key: String,
        default: Long
) : BasePreferenceImpl<Long>(preferences, key, default, LongSerializer), LongPreference {

    private object LongSerializer : Serializer<Long> {
        override fun serialize(storage: SharedPreferences.Editor, key: String, value: Long) {
            storage.putLong(key, value)
        }

        override fun unserialize(source: SharedPreferences, key: String, default: Long): Long {
            return source.getLong(key, default)
        }
    }
}
