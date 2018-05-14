package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.fragments.interactors.MultipleReportsMapInteractor
import lt.vilnius.tvarkau.mvp.interactors.ReportTypesInteractor
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

/**
 * @author Martynas Jurkus
 */
@Module
class TestDataModule {

    @Provides
    @Singleton
    fun provideReportTypesInteractor(): ReportTypesInteractor = mock()

    @Provides
    @Singleton
    fun provideMultipleReportsMapInteractor(): MultipleReportsMapInteractor = mock()

    @Provides
    @Singleton
    @RawOkHttpClient
    fun provideRawOkHttpClient(): OkHttpClient = mock()

    @Provides
    @Singleton
    fun provideCache(): Cache = Cache(File("/"), 1000)
}