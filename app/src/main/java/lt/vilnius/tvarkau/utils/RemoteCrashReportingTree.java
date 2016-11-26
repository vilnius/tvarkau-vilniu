package lt.vilnius.tvarkau.utils;


import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class RemoteCrashReportingTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        String messageWithTag = tag + ": " + message;

        FirebaseCrash.log(messageWithTag);
        Crashlytics.log(messageWithTag);

        if (t != null) {
            Crashlytics.logException(t);
            FirebaseCrash.report(t);
        } else if (priority > Log.WARN) {
            Throwable throwable = new Throwable(messageWithTag);

            Crashlytics.logException(throwable);
            FirebaseCrash.report(throwable);
        }
    }
}