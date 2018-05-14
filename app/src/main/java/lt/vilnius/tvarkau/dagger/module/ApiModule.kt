package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.api.TvarkauMiestaApi
import lt.vilnius.tvarkau.dagger.Api
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideApi(@Api adapter: Retrofit): TvarkauMiestaApi {
        return adapter.create(TvarkauMiestaApi::class.java)
    }
}