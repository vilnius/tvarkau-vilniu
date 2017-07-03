package lt.vilnius.tvarkau.dagger.module

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.navigation.BottomNavigationController
import lt.vilnius.tvarkau.navigation.FragmentTransactionExecutor
import lt.vilnius.tvarkau.navigation.NavigationManager


@Module
class MainActivityModule(private val activity: AppCompatActivity) {

    @Provides
    fun provideNavManager(): NavigationManager {
        return NavigationManager(activity,
                FragmentTransactionExecutor(
                        activity.supportFragmentManager))
    }

    @Provides
    fun provideBottomNavigationController(navigationManager: NavigationManager): BottomNavigationController {
        return BottomNavigationController(activity, navigationManager)
    }
}

