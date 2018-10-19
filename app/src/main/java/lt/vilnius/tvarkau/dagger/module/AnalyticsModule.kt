package lt.vilnius.tvarkau.dagger.module

import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.analytics.RemoteAnalytics
import javax.inject.Singleton

@Module
class AnalyticsModule {

    @Provides
    @Singleton
    fun providesMixpanel(application: TvarkauApplication): MixpanelAPI {
        return MixpanelAPI.getInstance(application, application.getString(R.string.mixpanel_token))
    }

    @Provides
    @Singleton
    fun providesAnalytics(application: TvarkauApplication, mixpanelAPI: MixpanelAPI): Analytics {
        return RemoteAnalytics(application.applicationContext, mixpanelAPI)
    }
}
