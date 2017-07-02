package lt.vilnius.tvarkau.dagger.component

import android.support.v7.app.AppCompatActivity
import dagger.Subcomponent
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.dagger.module.MainActivityModule
import lt.vilnius.tvarkau.fragments.AllReportsListFragment
import lt.vilnius.tvarkau.fragments.BaseMapFragment
import lt.vilnius.tvarkau.fragments.MultipleProblemsMapFragment
import lt.vilnius.tvarkau.fragments.MyReportsListFragment


@Subcomponent(modules = arrayOf(MainActivityModule::class))
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(allReportsListFragment: AllReportsListFragment)

    fun inject(myReportsListFragment: MyReportsListFragment)

    fun inject(baseMapFragment: BaseMapFragment)

    fun inject(fragment: MultipleProblemsMapFragment)


    companion object {
        fun init(applicationComponent: ApplicationComponent,
                 activity: AppCompatActivity): MainActivityComponent {
            return applicationComponent.mainActivityComponent(MainActivityModule(activity))
        }
    }
}