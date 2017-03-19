package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.prefs.LongPreference
import lt.vilnius.tvarkau.prefs.Preferences
import lt.vilnius.tvarkau.prefs.Preferences.LAST_DISPLAYED_PHOTO_INSTRUCTIONS
import lt.vilnius.tvarkau.prefs.Preferences.LIST_SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.LIST_SELECTED_FILTER_REPORT_TYPE
import lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_TYPE
import lt.vilnius.tvarkau.prefs.StringPreference
import javax.inject.Named
import javax.inject.Singleton

@Module
class TestSharedPreferencesModule {

    @Provides
    @Singleton
    @Named(Preferences.COMMON_PREFERENCES)
    fun providerApplicationPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(Preferences.COMMON_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named(MY_PROBLEMS_PREFERENCES)
    fun provideMyProblemPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(MY_PROBLEMS_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named(LAST_DISPLAYED_PHOTO_INSTRUCTIONS)
    fun providePhotoInstructions(): LongPreference = mock()


    @Provides
    @Singleton
    @Named(SELECTED_FILTER_REPORT_STATUS)
    fun provideReportStatusFilter(): StringPreference = mock()

    @Provides
    @Singleton
    @Named(SELECTED_FILTER_REPORT_TYPE)
    fun provideReportTypeFilter(): StringPreference = mock()

    @Provides
    @Singleton
    @Named(LIST_SELECTED_FILTER_REPORT_STATUS)
    fun provideReportListStatusFilter(): StringPreference = mock()

    @Provides
    @Singleton
    @Named(LIST_SELECTED_FILTER_REPORT_TYPE)
    fun provideReportListTypeFilter(): StringPreference = mock()
}
