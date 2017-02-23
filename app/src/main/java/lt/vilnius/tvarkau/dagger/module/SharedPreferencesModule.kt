package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.prefs.BooleanPreference
import lt.vilnius.tvarkau.prefs.BooleanPreferenceImpl
import lt.vilnius.tvarkau.prefs.Preferences.COMMON_PREFERENCES
import lt.vilnius.tvarkau.prefs.Preferences.DISPLAY_PHOTO_INSTRUCTIONS
import lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_TYPE
import lt.vilnius.tvarkau.prefs.StringPreference
import lt.vilnius.tvarkau.prefs.StringPreferenceImpl
import javax.inject.Named
import javax.inject.Singleton

@Module
class SharedPreferencesModule {

    @Provides
    @Singleton
    @Named(COMMON_PREFERENCES)
    fun providerApplicationPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(COMMON_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named(MY_PROBLEMS_PREFERENCES)
    fun provideMyProblemPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(MY_PROBLEMS_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named(DISPLAY_PHOTO_INSTRUCTIONS)
    fun providePhotoInstructions(
            @Named(COMMON_PREFERENCES) preference: SharedPreferences
    ): BooleanPreference {
        return BooleanPreferenceImpl(preference, DISPLAY_PHOTO_INSTRUCTIONS, true)
    }

    @Provides
    @Singleton
    @Named(SELECTED_FILTER_REPORT_STATUS)
    fun provideReportStatusFilter(
            @Named(COMMON_PREFERENCES) preference: SharedPreferences
    ): StringPreference {
        return StringPreferenceImpl(preference, SELECTED_FILTER_REPORT_STATUS, "")
    }

    @Provides
    @Singleton
    @Named(SELECTED_FILTER_REPORT_TYPE)
    fun provideReportTypeFilter(
            @Named(COMMON_PREFERENCES) preference: SharedPreferences
    ): StringPreference {
        return StringPreferenceImpl(preference, SELECTED_FILTER_REPORT_TYPE, "")
    }
}
