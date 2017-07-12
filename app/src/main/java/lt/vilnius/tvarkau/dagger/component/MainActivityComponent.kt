package lt.vilnius.tvarkau.dagger.component

import dagger.Subcomponent
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.dagger.module.MainActivityModule

@Subcomponent(modules = arrayOf(MainActivityModule::class))
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    companion object {
        fun init(
                activityComponent: ActivityComponent,
                mainActivity: MainActivity
        ): MainActivityComponent {
            return activityComponent.mainActivityComponent(MainActivityModule(mainActivity))
        }
    }

}