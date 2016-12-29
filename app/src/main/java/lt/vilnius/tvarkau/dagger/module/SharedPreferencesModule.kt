package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.prefs.BooleanPreference
import lt.vilnius.tvarkau.prefs.BooleanPreferenceImpl
import lt.vilnius.tvarkau.prefs.Preferences.DISPLAY_PHOTO_INSTRUCTIONS
import lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES
import lt.vilnius.tvarkau.prefs.Preferences.PREFS_NAME
import javax.inject.Named
import javax.inject.Singleton

@Module
class SharedPreferencesModule {

    @Provides
    @Singleton
    @Named(PREFS_NAME)
    fun providerApplicationPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
            @Named(PREFS_NAME) preference: SharedPreferences
    ): BooleanPreference {
        return BooleanPreferenceImpl(preference, DISPLAY_PHOTO_INSTRUCTIONS, true)
    }
}
