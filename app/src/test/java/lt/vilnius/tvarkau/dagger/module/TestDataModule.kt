package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.mvp.interactors.ReportTypesInteractor
import javax.inject.Singleton

/**
 * @author Martynas Jurkus
 */
@Module
class TestDataModule {

    @Provides
    @Singleton
    fun provideReportTypesInteractor(): ReportTypesInteractor = mock()
}