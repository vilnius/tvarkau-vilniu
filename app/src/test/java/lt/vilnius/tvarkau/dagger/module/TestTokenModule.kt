package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.auth.SessionToken
import javax.inject.Singleton

@Module
class TestTokenModule {

    @Provides
    @Singleton
    fun provideSessionToken(): SessionToken = mock()
}