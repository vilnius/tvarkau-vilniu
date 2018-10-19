package lt.vilnius.tvarkau.dagger.component

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.dagger.module.ActivityBuilder
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
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ViewModelModule::class,
        AnalyticsModule::class,
        DatabaseModule::class,
        SharedPreferencesModule::class,
        DataModule::class,
        TokenModule::class,
        RestAdapterModule::class,
        SerializationModule::class,
        ActivityBuilder::class,
        LegacyApiModule::class,
        ApiModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<TvarkauApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<TvarkauApplication>()
}
