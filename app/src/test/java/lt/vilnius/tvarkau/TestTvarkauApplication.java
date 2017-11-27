package lt.vilnius.tvarkau;

import com.squareup.leakcanary.RefWatcher;

import lt.vilnius.tvarkau.dagger.component.ApplicationComponent;
import lt.vilnius.tvarkau.dagger.component.DaggerTestApplicationComponent;
import lt.vilnius.tvarkau.dagger.component.TestApplicationComponent;
import lt.vilnius.tvarkau.dagger.module.TestAppModule;

/**
 * @author Martynas Jurkus
 */
public class TestTvarkauApplication extends TvarkauApplication {

    public TestApplicationComponent testComponent;

    @Override
    protected RefWatcher setupLeakCanary() {
        // No leakcanary in unit tests.
        return RefWatcher.DISABLED;
    }

    @Override
    protected ApplicationComponent buildComponent() {
        testComponent = DaggerTestApplicationComponent.builder()
                .testAppModule(new TestAppModule(this))
                .build();

        return testComponent;
    }

    @Override
    protected void initLibraries() {
        //nope, this is alternate reality
    }
}