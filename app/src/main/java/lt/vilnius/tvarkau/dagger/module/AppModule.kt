package lt.vilnius.tvarkau.dagger.module

import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.dagger.DbScheduler
import lt.vilnius.tvarkau.dagger.IoScheduler
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProviderImpl
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

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
    fun providesRefWatcher(
        application: TvarkauApplication
    ): RefWatcher {
        return application.refWatcher
    }

    @Provides
    @Singleton
    @DbScheduler
    fun provideDbScheduler(): Scheduler {
        return Schedulers.single()
    }

    @Provides
    @Singleton
    fun provideConnectivityProvider(
        application: TvarkauApplication
    ): ConnectivityProvider {
        return ConnectivityProviderImpl(application)
    }

    @Provides
    @Named("all_reports")
    fun provideAllReportTypes(): String {
        return ""
    }
}
