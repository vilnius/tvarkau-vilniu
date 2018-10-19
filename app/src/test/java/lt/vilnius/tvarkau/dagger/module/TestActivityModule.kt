package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lt.vilnius.tvarkau.fragments.NewReportFragment

@Module
abstract class TestActivityModule {

    @ContributesAndroidInjector
    abstract fun newReportFragment(): NewReportFragment
}

