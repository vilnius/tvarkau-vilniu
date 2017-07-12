package lt.vilnius.tvarkau.dagger.module

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
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

}

