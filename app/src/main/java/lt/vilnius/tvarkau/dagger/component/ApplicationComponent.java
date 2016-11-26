package lt.vilnius.tvarkau.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import lt.vilnius.tvarkau.AppModule;
import lt.vilnius.tvarkau.BaseActivity;
import lt.vilnius.tvarkau.SharedPreferencesModule;
import lt.vilnius.tvarkau.dagger.module.APIModule;
import lt.vilnius.tvarkau.dagger.module.LegacyApiModule;
import lt.vilnius.tvarkau.fragments.BaseFragment;
import lt.vilnius.tvarkau.fragments.BaseMapFragment;
import lt.vilnius.tvarkau.fragments.ReportImportDialogFragment;

/**
 * @author Martynas Jurkus
 */
@Singleton
@Component(modules = {
        LegacyApiModule.class,
        APIModule.class,
        SharedPreferencesModule.class,
        AppModule.class
})
public interface ApplicationComponent {

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

    void inject(BaseMapFragment fragment);

    void inject(ReportImportDialogFragment fragment);
}
