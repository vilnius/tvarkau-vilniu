package lt.vilnius.tvarkau.dagger.module

import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.TestTvarkauApplication
import lt.vilnius.tvarkau.analytics.Analytics
import javax.inject.Singleton

/**
 * @author Martynas Jurkus
 */
@Module
class TestAnalyticsModule {

    @Provides
    @Singleton
    internal fun providesMixpanel(application: TestTvarkauApplication): MixpanelAPI = mock()

    @Provides
    @Singleton
    fun providesAnalytics(application: TestTvarkauApplication): Analytics = mock()
}
