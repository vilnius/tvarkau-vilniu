package lt.vilnius.tvarkau.dagger.component

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import lt.vilnius.tvarkau.TestTvarkauApplication
import lt.vilnius.tvarkau.dagger.module.SerializationModule
import lt.vilnius.tvarkau.dagger.module.TestActivityBuilder
import lt.vilnius.tvarkau.dagger.module.TestAnalyticsModule
import lt.vilnius.tvarkau.dagger.module.TestApiModule
import lt.vilnius.tvarkau.dagger.module.TestAppModule
import lt.vilnius.tvarkau.dagger.module.TestDataModule
import lt.vilnius.tvarkau.dagger.module.TestDatabaseModule
import lt.vilnius.tvarkau.dagger.module.TestLegacyApiModule
import lt.vilnius.tvarkau.dagger.module.TestSharedPreferencesModule
import lt.vilnius.tvarkau.dagger.module.TestTokenModule
import lt.vilnius.tvarkau.dagger.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        TestLegacyApiModule::class,
        TestSharedPreferencesModule::class,
        TestAppModule::class,
        TestAnalyticsModule::class,
        TestDataModule::class,
        TestTokenModule::class,
        TestApiModule::class,
        TestDatabaseModule::class,
        ViewModelModule::class,
        SerializationModule::class,
        TestActivityBuilder::class
    ]
)
interface TestApplicationComponent : AndroidInjector<TestTvarkauApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<TestTvarkauApplication>()
}
