package lt.vilnius.tvarkau;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

    public static final String MY_PROBLEMS_PREFERENCES    = "my_problem_preferences";

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return application.getSharedPreferences(MY_PROBLEMS_PREFERENCES, Context.MODE_PRIVATE);
    }
}
