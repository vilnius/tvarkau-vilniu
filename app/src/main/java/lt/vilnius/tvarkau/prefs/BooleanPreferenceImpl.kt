package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences

class BooleanPreferenceImpl(preferences: SharedPreferences,
                            key: String,
                            default: Boolean) :
        BasePreferenceImpl<Boolean>(preferences, key, default, BooleanSerializer),
        BooleanPreference {

    private object BooleanSerializer : Serializer<Boolean> {
        override fun serialize(storage: SharedPreferences.Editor, key: String, value: Boolean) {
            storage.putBoolean(key, value)
        }

        override fun unserialize(source: SharedPreferences, key: String, default: Boolean): Boolean {
            return source.getBoolean(key, default)
        }
    }
}
