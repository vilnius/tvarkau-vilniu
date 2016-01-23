package lt.vilnius.tvarkau.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Managing user's shared Preferences.
 * At the time it is only used to track if user has opted for anonymous login
 * but later on it also will help us to store actual user information.
 */
public class SharedPrefsManager {
    private static final String PREFS_NAME = "TVARKAU-VILNIU_PREFS";

    private static final String PREF_ISANONYMOUS = "isUserAnonymous";
    private static final Boolean DEFAULT_PREF_ISANONYMOUS = true;

    private static SharedPrefsManager mSingleton = new SharedPrefsManager();
    private static Context mContext;


    public static SharedPrefsManager instance(Context context){
        mContext = context;
        return mSingleton;
    }

    public static SharedPrefsManager instance(){
        return mSingleton;
    }

    private static void init(Context context){
        mContext = context;
    }

    public SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public  Boolean getIsUserAnonymous() {
        return getPrefs().getBoolean(PREF_ISANONYMOUS, DEFAULT_PREF_ISANONYMOUS);
    }

    public void setUserAnonymous(boolean isAnonymous){
        SharedPreferences.Editor edit = getPrefs().edit();
        edit.putBoolean(PREF_ISANONYMOUS, isAnonymous);
        edit.apply();
    }
}
