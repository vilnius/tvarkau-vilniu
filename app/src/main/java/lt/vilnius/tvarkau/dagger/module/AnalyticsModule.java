package lt.vilnius.tvarkau.dagger.module;

import android.app.Application;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lt.vilnius.tvarkau.analytics.Analytics;
import lt.vilnius.tvarkau.analytics.RemoteAnalytics;

@Module
public class AnalyticsModule {

    private final String projectToken = "8f77b0f6ecba7677aec235170e8eee4f";

    @Provides
    @Singleton
    MixpanelAPI providesMixPanel(Application application) {
        return MixpanelAPI.getInstance(application, projectToken);
    }

    @Provides
    @Singleton
    Analytics providesAnalytics(Application application, MixpanelAPI mixpanelAPI) {
        return new RemoteAnalytics(application.getApplicationContext(), mixpanelAPI);
    }
}
