package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.dagger.DbScheduler
import lt.vilnius.tvarkau.dagger.IoScheduler
import lt.vilnius.tvarkau.dagger.UiScheduler
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProvider
import lt.vilnius.tvarkau.fragments.presenters.ConnectivityProviderImpl
import javax.inject.Named
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
    @DbScheduler
    fun provideDbScheduler(): Scheduler {
        return Schedulers.single()
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

    @Provides
    @Named("all_reports")
    fun provideAllReportTypes(): String {
        return application.resources.getString(R.string.report_filter_all_report_types)
    }
}
