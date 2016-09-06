package lt.vilnius.tvarkau;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class TvarkauApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        Stetho.initializeWithDefaults(this);
    }
}
