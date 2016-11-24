package lt.vilnius.tvarkau;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

public class LogApp {

    public static void logCrash(Throwable throwable) {
        Log.e("TvarkauVilniu", throwable.getMessage(), throwable);
        if (!BuildConfig.DEBUG) {
            FirebaseCrash.report(throwable);
            Crashlytics.logException(throwable);
        }
    }
}
