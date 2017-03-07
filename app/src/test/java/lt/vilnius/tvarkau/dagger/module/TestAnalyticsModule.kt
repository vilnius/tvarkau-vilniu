package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.analytics.Analytics
import javax.inject.Singleton

/**
 * @author Martynas Jurkus
 */
@Module
class TestAnalyticsModule {

    @Provides
    @Singleton
    internal fun providesMixpanel(application: Application): MixpanelAPI = mock()

    @Provides
    @Singleton
    fun providesAnalytics(application: Application): Analytics = mock()
}