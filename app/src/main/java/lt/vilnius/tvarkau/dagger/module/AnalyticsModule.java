package lt.vilnius.tvarkau.dagger.module;

import android.app.Application;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.analytics.Analytics;
import lt.vilnius.tvarkau.analytics.RemoteAnalytics;

@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    MixpanelAPI providesMixpanel(Application application) {
        return MixpanelAPI.getInstance(application, application.getString(R.string.mixpanel_token));
    }

    @Provides
    @Singleton
    Analytics providesAnalytics(Application application, MixpanelAPI mixpanelAPI) {
        return new RemoteAnalytics(application.getApplicationContext(), mixpanelAPI);
    }
}
