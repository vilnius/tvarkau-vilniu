package lt.vilnius.tvarkau;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;

import io.fabric.sdk.android.Fabric;
import lt.vilnius.tvarkau.utils.RemoteCrashReportingTree;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class TvarkauApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());


            Timber.plant(new RemoteCrashReportingTree());
        }

        Stetho.initializeWithDefaults(this);
        AndroidThreeTen.init(this);
    }
}
