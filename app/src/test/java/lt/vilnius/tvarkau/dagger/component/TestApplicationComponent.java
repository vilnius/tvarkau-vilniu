package lt.vilnius.tvarkau.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import lt.vilnius.tvarkau.SharedPreferencesModule;
import lt.vilnius.tvarkau.TestActivity;
import lt.vilnius.tvarkau.dagger.module.APIModule;
import lt.vilnius.tvarkau.dagger.module.TestAppModule;
import lt.vilnius.tvarkau.dagger.module.TestLegacyApiModule;
import lt.vilnius.tvarkau.fragments.ProblemDetailFragmentTest;

/**
 * @author Martynas Jurkus
 */

@Singleton
@Component(
        modules = {
                TestLegacyApiModule.class,
                APIModule.class,
                SharedPreferencesModule.class,
                TestAppModule.class
        }
)
public interface TestApplicationComponent extends ApplicationComponent {

    void inject(TestActivity activity);

    void inject(ProblemDetailFragmentTest fragment);
}
