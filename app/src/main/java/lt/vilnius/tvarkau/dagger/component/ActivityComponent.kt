package lt.vilnius.tvarkau.dagger.component

import android.support.v7.app.AppCompatActivity
import dagger.Subcomponent
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.dagger.module.ActivityModule
import lt.vilnius.tvarkau.dagger.module.MainActivityModule
import lt.vilnius.tvarkau.fragments.*


@Subcomponent(
    modules = [
        ActivityModule::class
    ]
)
interface ActivityComponent {

    fun mainActivityComponent(mainActivityModule: MainActivityModule): MainActivityComponent

    fun inject(allReportsListFragment: AllReportsListFragment)

    fun inject(myReportsListFragment: MyReportsListFragment)

    fun inject(baseMapFragment: BaseMapFragment)

    fun inject(fragment: MultipleProblemsMapFragment)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(activity: BaseActivity)

    fun inject(fragment: BaseFragment)

    fun inject(fragment: ReportImportDialogFragment)

    fun inject(fragment: NewReportFragment)

    fun inject(fragment: PhotoInstructionsFragment)

    fun inject(fragment: ReportTypeListFragment)

    fun inject(fragment: ReportFilterFragment)

    fun inject(fragment: ReportDetailsFragment)

    companion object {
        fun init(
            applicationComponent: ApplicationComponent,
            activity: AppCompatActivity
        ): ActivityComponent {
            return applicationComponent.activityComponent(ActivityModule(activity))
        }
    }

}
