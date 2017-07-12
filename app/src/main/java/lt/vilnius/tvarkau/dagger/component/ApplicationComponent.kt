package lt.vilnius.tvarkau.dagger.component

import dagger.Component
import lt.vilnius.tvarkau.AppModule
import lt.vilnius.tvarkau.dagger.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        LegacyApiModule::class,
        APIModule::class,
        SharedPreferencesModule::class,
        AppModule::class,
        AnalyticsModule::class,
        DataModule::class))
interface ApplicationComponent {

    fun activityComponent(activityModule: ActivityModule): ActivityComponent

}
