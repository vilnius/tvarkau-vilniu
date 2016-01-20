package lt.vilnius.tvarkau.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

/**
 * Created by Karolis Vycius on 2016-01-14.
 */
public class PermissionUtils {

    public static boolean verifyAndRequestPermissions(Activity activity, int requestCode,
                                                      String... permissions) {
        ArrayList<String> missingPermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (missingPermissions.isEmpty())
            return true;
        else {
            String[] missingPermissionsArr = missingPermissions.toArray(new String[missingPermissions.size()]);

            ActivityCompat.requestPermissions(activity, missingPermissionsArr, requestCode);
        }

        return false;
    }
}
