package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.navigation.NavigationManagerImpl


@Module
class NavigationModule {

    @Provides
    fun provideNavManager(activity: MainActivity): NavigationManager {
        return NavigationManagerImpl(activity)
    }
}
