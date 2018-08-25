package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.repository.ReportStatusDao
import lt.vilnius.tvarkau.repository.ReportTypeDao
import lt.vilnius.tvarkau.repository.ReportsDao
import javax.inject.Singleton


@Module
class TestDatabaseModule {

    @Singleton
    @Provides
    fun provideReportsDao(): ReportsDao = mock()

    @Singleton
    @Provides
    fun provideReportTypesDao(): ReportTypeDao = mock()

    @Singleton
    @Provides
    fun provideReportStatusesDao(): ReportStatusDao = mock()
}
