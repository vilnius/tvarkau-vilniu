package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.TestTvarkauApplication
import lt.vilnius.tvarkau.dagger.DbScheduler
import lt.vilnius.tvarkau.dagger.IoScheduler
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import javax.inject.Singleton

@Module
class TestAppModule() {

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
    @DbScheduler
    fun provideDbScheduler(): Scheduler {
        return Schedulers.trampoline()
    }

    @Provides
    @Singleton
    internal fun providesRefWatcher(application: TestTvarkauApplication): RefWatcher {
        return application.refWatcher
    }

    @Provides
    @Singleton
    fun provideConnectivityProvider(): ConnectivityProvider = mock()
}
