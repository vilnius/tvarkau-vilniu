package lt.vilnius.tvarkau.dagger.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lt.vilnius.tvarkau.analytics.Analytics;

@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    Analytics providesAnalytics(Application application) {
        return new Analytics(application.getApplicationContext());
    }
}
