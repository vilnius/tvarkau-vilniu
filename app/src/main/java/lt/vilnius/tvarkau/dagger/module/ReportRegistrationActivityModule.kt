package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lt.vilnius.tvarkau.fragments.NewReportFragment
import lt.vilnius.tvarkau.fragments.PhotoInstructionsFragment
import lt.vilnius.tvarkau.fragments.ReportTypeListFragment

@Module
abstract class ReportRegistrationActivityModule {

    @ContributesAndroidInjector
    abstract fun reportTypeListFragment(): ReportTypeListFragment

    @ContributesAndroidInjector
    abstract fun newReportFragment(): NewReportFragment

    @ContributesAndroidInjector
    abstract fun photoInsturctionsFragment(): PhotoInstructionsFragment
}
