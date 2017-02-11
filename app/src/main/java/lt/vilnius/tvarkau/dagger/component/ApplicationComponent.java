package lt.vilnius.tvarkau.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import lt.vilnius.tvarkau.AppModule;
import lt.vilnius.tvarkau.BaseActivity;
import lt.vilnius.tvarkau.dagger.module.APIModule;
import lt.vilnius.tvarkau.dagger.module.AnalyticsModule;
import lt.vilnius.tvarkau.dagger.module.DataModule;
import lt.vilnius.tvarkau.dagger.module.LegacyApiModule;
import lt.vilnius.tvarkau.dagger.module.SharedPreferencesModule;
import lt.vilnius.tvarkau.fragments.BaseFragment;
import lt.vilnius.tvarkau.fragments.BaseMapFragment;
import lt.vilnius.tvarkau.fragments.NewReportFragment;
import lt.vilnius.tvarkau.fragments.PhotoInstructionsFragment;
import lt.vilnius.tvarkau.fragments.ReportImportDialogFragment;
import lt.vilnius.tvarkau.fragments.ReportTypeListFragment;

/**
 * @author Martynas Jurkus
 */
@Singleton
@Component(modules = {
        LegacyApiModule.class,
        APIModule.class,
        SharedPreferencesModule.class,
        AppModule.class,
        AnalyticsModule.class,
        DataModule.class
})
public interface ApplicationComponent {

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

    void inject(BaseMapFragment fragment);

    void inject(ReportImportDialogFragment fragment);

    void inject(NewReportFragment fragment);

    void inject(PhotoInstructionsFragment fragment);

    void inject(ReportTypeListFragment fragment);
}
