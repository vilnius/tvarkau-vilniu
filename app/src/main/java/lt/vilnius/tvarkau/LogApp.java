package lt.vilnius.tvarkau;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

public class LogApp {

    public static void logCrash(Throwable throwable) {
        throwable.printStackTrace();
        FirebaseCrash.report(throwable);
        Crashlytics.logException(throwable);
    }
}
