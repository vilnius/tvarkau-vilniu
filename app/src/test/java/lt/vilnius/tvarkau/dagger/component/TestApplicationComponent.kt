package lt.vilnius.tvarkau.dagger.component

import dagger.Component
import lt.vilnius.tvarkau.dagger.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    TestLegacyApiModule::class,
    APIModule::class,
    TestSharedPreferencesModule::class,
    TestAppModule::class,
    TestAnalyticsModule::class,
    TestDataModule::class,
    TestTokenModule::class])
interface TestApplicationComponent : ApplicationComponent {

    fun activityComponent(testActivityModule: TestActivityModule): TestActivityComponent
}
