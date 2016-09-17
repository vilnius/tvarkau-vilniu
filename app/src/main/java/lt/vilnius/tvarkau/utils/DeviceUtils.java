package lt.vilnius.tvarkau.utils;

import android.os.Build;

import java.util.Locale;

import lt.vilnius.tvarkau.BuildConfig;

public class DeviceUtils {

    public static String getAppVersion() {
        return String.format(Locale.getDefault(), "%s.%d",
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        );
    }

    public static String getDeviceInfo() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        if (model.startsWith(manufacturer)) {
            return  capitalize(model) + ", " + "Android " + androidVersion;
        } else {
            return capitalize(manufacturer) + " " + model + ", " + "Android " + androidVersion;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
