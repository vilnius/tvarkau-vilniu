package lt.vilnius.tvarkau.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import lt.vilnius.tvarkau.TestActivity;
import lt.vilnius.tvarkau.dagger.module.APIModule;
import lt.vilnius.tvarkau.dagger.module.TestAnalyticsModule;
import lt.vilnius.tvarkau.dagger.module.TestAppModule;
import lt.vilnius.tvarkau.dagger.module.TestLegacyApiModule;
import lt.vilnius.tvarkau.dagger.module.TestSharedPreferencesModule;
import lt.vilnius.tvarkau.fragments.NewReportFragmentTest;
import lt.vilnius.tvarkau.fragments.ProblemDetailFragmentTest;

/**
 * @author Martynas Jurkus
 */

@Singleton
@Component(
        modules = {
                TestLegacyApiModule.class,
                APIModule.class,
                TestSharedPreferencesModule.class,
                TestAppModule.class,
                TestAnalyticsModule.class
        }
)
public interface TestApplicationComponent extends ApplicationComponent {

    void inject(TestActivity activity);

    void inject(ProblemDetailFragmentTest test);

    void inject(NewReportFragmentTest test);
}
