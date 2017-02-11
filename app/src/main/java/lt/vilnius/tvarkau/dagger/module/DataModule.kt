package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.mvp.interactors.ReportTypesInteractor
import lt.vilnius.tvarkau.mvp.interactors.ReportTypesInteractorImpl
import rx.Scheduler
import javax.inject.Singleton

/**
 * @author Martynas Jurkus
 */
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideReportTypesInteractor(
            legacyApi: LegacyApiService,
            @IoScheduler ioScheduler: Scheduler
    ): ReportTypesInteractor {
        return ReportTypesInteractorImpl(legacyApi, ioScheduler)
    }
}