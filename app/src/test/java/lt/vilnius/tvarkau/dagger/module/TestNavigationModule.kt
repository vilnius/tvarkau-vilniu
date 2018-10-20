package lt.vilnius.tvarkau.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.navigation.NavigationManager

@Module
class TestNavigationModule {

    @Provides
    fun provideNavManager(): NavigationManager = mock()
}
