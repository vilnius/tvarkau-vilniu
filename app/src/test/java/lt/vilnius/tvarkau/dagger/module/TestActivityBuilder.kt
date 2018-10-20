package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lt.vilnius.tvarkau.TestActivity

@Module
abstract class TestActivityBuilder {

    @ContributesAndroidInjector(modules = [TestNavigationModule::class, TestActivityModule::class])
    abstract fun testActivity(): TestActivity

}
