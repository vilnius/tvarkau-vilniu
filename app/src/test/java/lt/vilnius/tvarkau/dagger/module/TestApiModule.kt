package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import javax.inject.Singleton

@Module
class TestApiModule {

    @Provides
    @Singleton
    fun provideApi(): TvarkauMiestaApi = mock()
}
