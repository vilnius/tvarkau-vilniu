package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.backend.LegacyApiService
import javax.inject.Singleton

@Module
class TestLegacyApiModule {

    @Provides
    @Singleton
    fun provideProblemService(): LegacyApiService = mock()
}
