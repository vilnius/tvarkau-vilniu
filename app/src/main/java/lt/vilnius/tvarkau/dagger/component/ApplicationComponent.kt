package lt.vilnius.tvarkau.dagger.component

import dagger.Component
import lt.vilnius.tvarkau.dagger.module.*
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
    ApiModule::class
])
interface ApplicationComponent {

    fun activityComponent(activityModule: ActivityModule): ActivityComponent

}
