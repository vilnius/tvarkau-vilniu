package lt.vilnius.tvarkau.dagger.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import lt.vilnius.tvarkau.fragments.AllReportsListFragment
import lt.vilnius.tvarkau.fragments.MultipleProblemsMapFragment
import lt.vilnius.tvarkau.fragments.MyReportsListFragment
import lt.vilnius.tvarkau.fragments.ReportDetailsFragment
import lt.vilnius.tvarkau.fragments.ReportFilterFragment
import lt.vilnius.tvarkau.fragments.ReportImportDialogFragment
import lt.vilnius.tvarkau.fragments.SettingsFragment


@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun allReportsListFragment(): AllReportsListFragment

    @ContributesAndroidInjector
    abstract fun myReportsListFragment(): MyReportsListFragment

    @ContributesAndroidInjector
    abstract fun multipleReportsMapFragment(): MultipleProblemsMapFragment

    @ContributesAndroidInjector
    abstract fun settingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun reportFilterFragment(): ReportFilterFragment

    @ContributesAndroidInjector
    abstract fun reportImportFragment(): ReportImportDialogFragment

    @ContributesAndroidInjector
    abstract fun reportDetailsFragment(): ReportDetailsFragment
}

