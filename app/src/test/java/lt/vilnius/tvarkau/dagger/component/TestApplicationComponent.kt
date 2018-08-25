package lt.vilnius.tvarkau.dagger.component

import dagger.Component
import lt.vilnius.tvarkau.dagger.module.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TestLegacyApiModule::class,
        TestSharedPreferencesModule::class,
        TestAppModule::class,
        TestAnalyticsModule::class,
        TestDataModule::class,
        TestTokenModule::class,
        TestApiModule::class,
        TestDatabaseModule::class,
        ViewModelModule::class,
        SerializationModule::class
    ]
)
interface TestApplicationComponent : ApplicationComponent {

    fun activityComponent(testActivityModule: TestActivityModule): TestActivityComponent
}
