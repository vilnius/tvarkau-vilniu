package lt.vilnius.tvarkau.dagger.module

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.navigation.BottomNavigationController
import lt.vilnius.tvarkau.navigation.FragmentTransactionExecutor
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.navigation.NavigationManagerImpl


@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @Provides
    fun provideNavManager(): NavigationManager {
        return NavigationManagerImpl(activity,
                FragmentTransactionExecutor(
                        activity.supportFragmentManager))
    }


    // TODO this belongs to MainActivity. Move to MainActivityModule
    @Provides
    fun provideBottomNavigationController(navigationManager: NavigationManager): BottomNavigationController {
        return BottomNavigationController(activity, navigationManager)
    }
}

