package lt.vilnius.tvarkau;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;

import io.fabric.sdk.android.Fabric;
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent;
import lt.vilnius.tvarkau.dagger.component.DaggerApplicationComponent;
import lt.vilnius.tvarkau.utils.RemoteCrashReportingTree;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class TvarkauApplication extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        inject();

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());

            Timber.plant(new RemoteCrashReportingTree());
        }

        initLibraries();
    }

    private void inject() {
        component = buildComponent();
    }

    protected void initLibraries() {
        Stetho.initializeWithDefaults(this);
        AndroidThreeTen.init(this);
    }

    protected ApplicationComponent buildComponent() {
        return DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }

}
