package lt.vilnius.tvarkau.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import lt.vilnius.tvarkau.dagger.module.APIModule;
import lt.vilnius.tvarkau.dagger.module.TestActivityModule;
import lt.vilnius.tvarkau.dagger.module.TestAnalyticsModule;
import lt.vilnius.tvarkau.dagger.module.TestAppModule;
import lt.vilnius.tvarkau.dagger.module.TestDataModule;
import lt.vilnius.tvarkau.dagger.module.TestLegacyApiModule;
import lt.vilnius.tvarkau.dagger.module.TestSharedPreferencesModule;

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
                TestAnalyticsModule.class,
                TestDataModule.class
        }
)
public interface TestApplicationComponent extends ApplicationComponent {

    TestActivityComponent activityComponent(TestActivityModule testActivityModule);

}
