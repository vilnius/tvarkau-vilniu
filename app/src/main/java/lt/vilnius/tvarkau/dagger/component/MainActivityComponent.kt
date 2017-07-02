package lt.vilnius.tvarkau.dagger.component

import android.support.v7.app.AppCompatActivity
import dagger.Subcomponent
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.dagger.module.MainActivityModule


@Subcomponent(modules = arrayOf(MainActivityModule::class))
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    companion object {
        fun init(applicationComponent: ApplicationComponent,
                 activity: AppCompatActivity): MainActivityComponent {
            return applicationComponent.mainActivityComponent(MainActivityModule(activity))
        }
    }
}