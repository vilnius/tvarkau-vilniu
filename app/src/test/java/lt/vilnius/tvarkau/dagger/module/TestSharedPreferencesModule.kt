package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.mock
import com.vinted.preferx.LongPreference
import com.vinted.preferx.ObjectPreference
import com.vinted.preferx.StringPreference
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.auth.ApiToken
import lt.vilnius.tvarkau.entity.City
import lt.vilnius.tvarkau.prefs.AppPreferences
import lt.vilnius.tvarkau.prefs.Preferences
import lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES
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
    fun provideAppPreferences(
        @Named(Preferences.COMMON_PREFERENCES) preference: SharedPreferences
    ): AppPreferences {
        return object : AppPreferences {
            override val apiToken: ObjectPreference<ApiToken> = mock()
            override val photoInstructionsLastSeen: LongPreference = mock()
            override val reportStatusSelectedFilter: StringPreference = mock()
            override val reportTypeSelectedFilter: StringPreference = mock()
            override val reportStatusSelectedListFilter: StringPreference = mock()
            override val reportTypeSelectedListFilter: StringPreference = mock()
            override val selectedCity: ObjectPreference<City> = mock()
        }
    }
}
