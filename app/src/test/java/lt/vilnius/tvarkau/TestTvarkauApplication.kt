package lt.vilnius.tvarkau

import com.squareup.leakcanary.RefWatcher
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import lt.vilnius.tvarkau.dagger.component.DaggerTestApplicationComponent

class TestTvarkauApplication : TvarkauApplication() {

    override fun setupLeakCanary(): RefWatcher {
        // No leakcanary in unit tests.
        return RefWatcher.DISABLED
    }

    override fun initLibraries() {
        //nope, this is alternate reality
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerTestApplicationComponent.builder().create(this)
    }
}
