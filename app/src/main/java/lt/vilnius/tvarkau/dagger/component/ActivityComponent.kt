package lt.vilnius.tvarkau.dagger.component

import android.support.v7.app.AppCompatActivity
import dagger.Subcomponent
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.dagger.module.ActivityModule
import lt.vilnius.tvarkau.fragments.*


@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

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


    companion object {
        fun init(
                applicationComponent: ApplicationComponent,
                activity: AppCompatActivity
        ): ActivityComponent {
            return applicationComponent.mainActivityComponent(ActivityModule(activity))
        }
    }

}