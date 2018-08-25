package lt.vilnius.tvarkau.dagger.component

import dagger.Component
import lt.vilnius.tvarkau.dagger.module.ActivityModule
import lt.vilnius.tvarkau.dagger.module.AnalyticsModule
import lt.vilnius.tvarkau.dagger.module.ApiModule
import lt.vilnius.tvarkau.dagger.module.AppModule
import lt.vilnius.tvarkau.dagger.module.DataModule
import lt.vilnius.tvarkau.dagger.module.DatabaseModule
import lt.vilnius.tvarkau.dagger.module.LegacyApiModule
import lt.vilnius.tvarkau.dagger.module.RestAdapterModule
import lt.vilnius.tvarkau.dagger.module.SerializationModule
import lt.vilnius.tvarkau.dagger.module.SharedPreferencesModule
import lt.vilnius.tvarkau.dagger.module.TokenModule
import lt.vilnius.tvarkau.dagger.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    LegacyApiModule::class,
    SharedPreferencesModule::class,
    AppModule::class,
    AnalyticsModule::class,
    DataModule::class,
    TokenModule::class,
    RestAdapterModule::class,
    SerializationModule::class,
    ApiModule::class,
    ViewModelModule::class,
    DatabaseModule::class
])
interface ApplicationComponent {

    fun activityComponent(activityModule: ActivityModule): ActivityComponent
}
