package lt.vilnius.tvarkau.prefs

import android.content.SharedPreferences
import lt.vilnius.tvarkau.data.GsonSerializer

class ObjectPreferenceImpl<T : Any>(
        prefs: SharedPreferences,
        key: String,
        default: T,
        gsonSerializer: GsonSerializer,
        clazz: Class<T>
) : BasePreferenceImpl<T>(
        prefs,
        key,
        default,
        GsonPrefSerializer(gsonSerializer, clazz)
), ObjectPreference<T>
