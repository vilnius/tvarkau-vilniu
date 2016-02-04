package lt.vilnius.tvarkau.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Karolis Vycius on 2016-01-14.
 */
public class PermissionUtils {

    public static boolean isAllPermissionsGranted(@NonNull Activity activity,
                                                  @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionUtils", permission + " is not granted.");
                return false;
            }
        }

        return true;
    }
}
