package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lt.vilnius.tvarkau.AboutActivity
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.activity.LoginActivity
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [NavigationModule::class, MainActivityModule::class])
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [ReportRegistrationActivityModule::class])
    abstract fun registrationActivity(): ReportRegistrationActivity

    @ContributesAndroidInjector
    abstract fun aboutActivity(): AboutActivity
}
