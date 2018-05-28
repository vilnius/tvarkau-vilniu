package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import com.nhaarman.mockito_kotlin.mock
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.TestTvarkauApplication
import lt.vilnius.tvarkau.dagger.IoScheduler
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import javax.inject.Singleton

/**
 * @author Martynas Jurkus
 */
@Module
class TestAppModule(val application: TestTvarkauApplication) {

    @Provides
    @Singleton
    fun providesApplication(): Application = application

    @Provides
    @Singleton
    @IoScheduler
    fun provideIoScheduler(): Scheduler = Schedulers.trampoline()

    @Provides
    @Singleton
    @UiScheduler
    fun provideUiScheduler(): Scheduler = Schedulers.trampoline()

    @Provides
    @Singleton
    internal fun providesRefWatcher(): RefWatcher {
        return application.refWatcher
    }

    @Provides
    @Singleton
    fun provideConnectivityProvider(): ConnectivityProvider = mock()
}