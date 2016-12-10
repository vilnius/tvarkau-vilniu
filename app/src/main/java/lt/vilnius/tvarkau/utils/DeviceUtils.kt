package lt.vilnius.tvarkau.utils

import android.os.Build
import lt.vilnius.tvarkau.BuildConfig

object DeviceUtils {

    val appVersion: String
        get() = BuildConfig.VERSION_NAME

    val deviceInfo: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val androidVersion = Build.VERSION.RELEASE
            
            if (model.startsWith(manufacturer)) {
                return "${model.capitalize()}, Android $androidVersion"
            } else
                return "${manufacturer.capitalize()} $model, Android $androidVersion"
        }
}
