package lt.vilnius.tvarkau

import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import lt.vilnius.tvarkau.dagger.component.DaggerApplicationComponent
import lt.vilnius.tvarkau.utils.RemoteCrashReportingTree
import timber.log.Timber
import timber.log.Timber.DebugTree


open class TvarkauApplication : DaggerApplication() {

    lateinit var refWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()

        refWatcher = setupLeakCanary()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(RemoteCrashReportingTree())
        }

        initLibraries()
    }

    protected open fun setupLeakCanary(): RefWatcher {
        return if (LeakCanary.isInAnalyzerProcess(this)) {
            RefWatcher.DISABLED
        } else LeakCanary.install(this)
    }

    protected open fun initLibraries() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
        Stetho.initializeWithDefaults(this)
        AndroidThreeTen.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerApplicationComponent.builder().create(this)
    }
}
