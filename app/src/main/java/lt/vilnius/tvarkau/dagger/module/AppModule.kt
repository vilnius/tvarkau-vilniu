package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.dagger.IoScheduler
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProviderImpl
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
    fun provideIoScheduler(): io.reactivex.Scheduler {
        return Schedulers.io()
    }

    @Provides
    @Singleton
    @UiScheduler
    fun provideUiScheduler(): io.reactivex.Scheduler {
        return AndroidSchedulers.mainThread()
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
