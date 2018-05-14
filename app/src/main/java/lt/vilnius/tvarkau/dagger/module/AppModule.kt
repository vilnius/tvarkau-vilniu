package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.dagger.IoScheduler2
import lt.vilnius.tvarkau.dagger.UiScheduler2
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProviderImpl
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Singleton

@Module
class AppModule(
        private val application: TvarkauApplication
) {

    @Provides
    @Singleton
    fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    @IoScheduler
    fun provideIoScheduler(): Scheduler {
        return Schedulers.io()
    }

    @Provides
    @Singleton
    @UiScheduler
    fun provideUiScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Provides
    @Singleton
    @IoScheduler2
    fun provideIoScheduler2(): io.reactivex.Scheduler {
        return io.reactivex.schedulers.Schedulers.io()
    }

    @Provides
    @Singleton
    @UiScheduler2
    fun provideUiScheduler2(): io.reactivex.Scheduler {
        return io.reactivex.android.schedulers.AndroidSchedulers.mainThread()
    }

    @Provides
    @Singleton
    fun providesRefWatcher(): RefWatcher {
        return application.refWatcher
    }

    @Provides
    @Singleton
    fun provideConnectivityProvider(): ConnectivityProvider {
        return ConnectivityProviderImpl(application)
    }
}