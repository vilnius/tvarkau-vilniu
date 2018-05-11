package lt.vilnius.tvarkau.utils;


import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class RemoteCrashReportingTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        String messageWithTag = tag + ": " + message;

        Crashlytics.log(messageWithTag);

        if (t != null) {
            Crashlytics.logException(t);
        } else if (priority > Log.WARN) {
            Throwable throwable = new Throwable(messageWithTag);

            Crashlytics.logException(throwable);
        }
    }
}