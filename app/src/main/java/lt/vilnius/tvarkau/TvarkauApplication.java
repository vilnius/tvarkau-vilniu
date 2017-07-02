package lt.vilnius.tvarkau;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import lt.vilnius.tvarkau.dagger.component.ApplicationComponent;
import lt.vilnius.tvarkau.dagger.component.DaggerApplicationComponent;
import lt.vilnius.tvarkau.utils.RemoteCrashReportingTree;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class TvarkauApplication extends Application {

    private ApplicationComponent component;

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);

        inject();

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
            FirebaseCrash.setCrashCollectionEnabled(false);
        } else {
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

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }
}
