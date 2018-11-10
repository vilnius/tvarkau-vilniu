package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lt.vilnius.tvarkau.AboutActivity
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.ReportDetailsActivity
import lt.vilnius.tvarkau.activity.LoginActivity
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.activity.ViispLoginActivity

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [NavigationModule::class, MainActivityModule::class])
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [ReportRegistrationActivityModule::class])
    abstract fun registrationActivity(): ReportRegistrationActivity

    @ContributesAndroidInjector(modules = [ReportDetailsActivityModule::class])
    abstract fun reportDetailsActivity(): ReportDetailsActivity

    @ContributesAndroidInjector
    abstract fun aboutActivity(): AboutActivity

    @ContributesAndroidInjector
    abstract fun viispLoginActivity(): ViispLoginActivity
}
