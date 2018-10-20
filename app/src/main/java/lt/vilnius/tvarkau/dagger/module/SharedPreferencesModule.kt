package lt.vilnius.tvarkau.dagger.module

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.data.GsonSerializer
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractor
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractorImpl
import lt.vilnius.tvarkau.prefs.AppPreferences
import lt.vilnius.tvarkau.prefs.AppPreferencesImpl
import lt.vilnius.tvarkau.prefs.Preferences.COMMON_PREFERENCES
import lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES
import lt.vilnius.tvarkau.utils.SharedPrefsManager
import javax.inject.Named
import javax.inject.Singleton

@Module
class SharedPreferencesModule {

    @Provides
    @Singleton
    @Named(COMMON_PREFERENCES)
    fun providerApplicationPreferences(application: TvarkauApplication): SharedPreferences {
        return application.getSharedPreferences(COMMON_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named(MY_PROBLEMS_PREFERENCES)
    fun provideMyProblemPreferences(application: TvarkauApplication): SharedPreferences {
        return application.getSharedPreferences(MY_PROBLEMS_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(
        @Named(COMMON_PREFERENCES) preference: SharedPreferences,
        gsonSerializer: GsonSerializer
    ): AppPreferences {
        return AppPreferencesImpl(preference, gsonSerializer)
    }


    @Provides
    internal fun providePersonalData(application: TvarkauApplication): PersonalDataInteractor {
        return PersonalDataInteractorImpl(SharedPrefsManager.getInstance(application))
    }
}
