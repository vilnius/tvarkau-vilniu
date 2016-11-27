package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import dagger.Module
import dagger.Provides
import rx.Scheduler
import rx.schedulers.Schedulers
import javax.inject.Singleton

/**
 * @author Martynas Jurkus
 */
@Module
class TestAppModule(val application: Application) {

    @Provides
    @Singleton
    fun providesApplication(): Application = application

    @Provides
    @Singleton
    @IoScheduler
    fun provideIoScheduler(): Scheduler = Schedulers.immediate()

    @Provides
    @Singleton
    @UiScheduler
    fun provideUiScheduler(): Scheduler = Schedulers.immediate()
}