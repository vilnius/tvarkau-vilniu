package lt.vilnius.tvarkau.utils;

import android.os.Build;

public class DeviceUtils {

    public static String getDeviceInfo() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        if (model.startsWith(manufacturer)) {
            return " (" + capitalize(model) + ", " + "Android " + androidVersion + ")";
        } else {
            return " (" + capitalize(manufacturer) + " " + model + ", " + "Android " + androidVersion + ")";
        }
    }

    public static String capitalize(String s) {
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
