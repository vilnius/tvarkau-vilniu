package lt.vilnius.tvarkau.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lt.vilnius.tvarkau.entity.Profile;

/**
 * Managing user's shared Preferences.
 * At the time it is only used to track if user has opted for anonymous login
 * but later on it also will help us to store actual user information.
 */
public class SharedPrefsManager {
    private static final String PREFS_NAME = "TVARKAU-VILNIU_PREFS";


    private static final String PREF_USER_PROFILE = "UserProfile";

    private static SharedPrefsManager mSingleton;
    private static SharedPreferences mSharedPrefences;


    private SharedPrefsManager(Context context) {
        mSharedPrefences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static void initializeInstance(Context context) {
        if (mSingleton == null) {
            mSingleton = new SharedPrefsManager(context);
        }
    }

    public static SharedPrefsManager getInstance(Context context) {
        initializeInstance(context.getApplicationContext());
        return mSingleton;
    }


    public Boolean isUserAnonymous() {
        return null == mSharedPrefences.getString(PREF_USER_PROFILE, null);
    }

    public void saveUserDetails(Profile profile) {
        SharedPreferences.Editor edit = mSharedPrefences.edit();
        String json = profile.createJsonData();
        edit.putString(PREF_USER_PROFILE, json);
        edit.apply();
    }

    @Nullable
    public Profile getUserProfile() {
        try {
            Gson gson = new Gson();
            String json = mSharedPrefences.getString(PREF_USER_PROFILE, null);
            if (json != null) {
                return gson.fromJson(json, Profile.class);
            } else {
                return new Profile();
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
            return null;
        }
    }

}
