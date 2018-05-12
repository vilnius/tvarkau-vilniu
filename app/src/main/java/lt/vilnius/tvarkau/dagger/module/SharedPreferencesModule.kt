package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.auth.ApiToken
import lt.vilnius.tvarkau.data.GsonSerializer
import lt.vilnius.tvarkau.prefs.*
import lt.vilnius.tvarkau.prefs.Preferences.API_TOKEN
import lt.vilnius.tvarkau.prefs.Preferences.COMMON_PREFERENCES
import lt.vilnius.tvarkau.prefs.Preferences.LAST_DISPLAYED_PHOTO_INSTRUCTIONS
import lt.vilnius.tvarkau.prefs.Preferences.LIST_SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.LIST_SELECTED_FILTER_REPORT_TYPE
import lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_STATUS
import lt.vilnius.tvarkau.prefs.Preferences.SELECTED_FILTER_REPORT_TYPE
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
    @Named(LAST_DISPLAYED_PHOTO_INSTRUCTIONS)
    fun providePhotoInstructions(
            @Named(COMMON_PREFERENCES) preference: SharedPreferences
    ): LongPreference {
        return LongPreferenceImpl(preference, LAST_DISPLAYED_PHOTO_INSTRUCTIONS, 0L)
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

    @Provides
    @Singleton
    @Named(LIST_SELECTED_FILTER_REPORT_STATUS)
    fun provideReportListStatusFilter(
            @Named(COMMON_PREFERENCES) preference: SharedPreferences
    ): StringPreference {
        return StringPreferenceImpl(preference, LIST_SELECTED_FILTER_REPORT_STATUS, "")
    }

    @Provides
    @Singleton
    @Named(LIST_SELECTED_FILTER_REPORT_TYPE)
    fun provideReportListTypeFilter(
            @Named(COMMON_PREFERENCES) preference: SharedPreferences
    ): StringPreference {
        return StringPreferenceImpl(preference, LIST_SELECTED_FILTER_REPORT_TYPE, "")
    }

    @Provides
    @Singleton
    @Named(API_TOKEN)
    fun provideApiToken(
            @Named(COMMON_PREFERENCES) preference: SharedPreferences,
            gsonSerializer: GsonSerializer
    ): ObjectPreference<ApiToken> {
        return ObjectPreferenceImpl<ApiToken>(
                prefs = preference,
                key = API_TOKEN,
                default = ApiToken(),
                gsonSerializer = gsonSerializer,
                clazz = ApiToken::class.java
        )
    }
}
