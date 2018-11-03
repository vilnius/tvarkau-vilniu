package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lt.vilnius.tvarkau.fragments.ReportDetailsFragment

@Module
abstract class ReportDetailsActivityModule {

    @ContributesAndroidInjector
    abstract fun reportDetailsFragment(): ReportDetailsFragment
}
