package lt.vilnius.tvarkau.dagger.component

import android.support.v7.app.AppCompatActivity
import dagger.Subcomponent
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.dagger.module.MainActivityModule
import lt.vilnius.tvarkau.fragments.*


@Subcomponent(modules = arrayOf(MainActivityModule::class))
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(allReportsListFragment: AllReportsListFragment)

    fun inject(myReportsListFragment: MyReportsListFragment)

    fun inject(baseMapFragment: BaseMapFragment)

    fun inject(fragment: MultipleProblemsMapFragment)

    fun inject(settingsFragment: SettingsFragment)


    companion object {
        fun init(
                applicationComponent: ApplicationComponent,
                activity: AppCompatActivity
        ): MainActivityComponent {
            return applicationComponent.mainActivityComponent(MainActivityModule(activity))
        }
    }

}