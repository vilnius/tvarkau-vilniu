package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.navigation.BottomNavigationController
import lt.vilnius.tvarkau.navigation.NavigationManager

@Module
class MainActivityModule(private val mainActivity: MainActivity) {

    @Provides
    fun provideBottomNavigationController(navigationManager: NavigationManager): BottomNavigationController {
        return BottomNavigationController(mainActivity, navigationManager)
    }
}