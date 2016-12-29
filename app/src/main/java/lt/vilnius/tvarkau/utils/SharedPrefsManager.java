package lt.vilnius.tvarkau.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.prefs.Preferences;
import timber.log.Timber;

/**
 * Managing user's shared Preferences.
 * At the time it is only used to track if user has opted for anonymous login
 * but later on it also will help us to store actual user information.
 */
public class SharedPrefsManager {

    private static final String PREF_USER_PROFILE = "UserProfile";
    private static final String PREF_USER_ANONYMOUS = "UserAnonymous";
    private static final String PREF_USER_SESSION_ID = "UserSessionId";
    private static final String PREF_USER_EMAIL = "UserEmail";
    private static final String PREF_USER_PASSWORD = "UserPassword";
    private static final String PREF_USER_LAST_IMPORT_DATE = "UserLastReportImportDate";
    private static final String PREF_USER_REMEMBER_ME_STATUS = "UserRememberMeStatus";


    private static SharedPrefsManager singleton;
    private static SharedPreferences sharedPreferences;


    private SharedPrefsManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Preferences.PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static void initializeInstance(Context context) {
        if (singleton == null) {
            singleton = new SharedPrefsManager(context);
        }
    }

    public static SharedPrefsManager getInstance(Context context) {
        initializeInstance(context.getApplicationContext());
        return singleton;
    }

    public boolean isUserAnonymous() {
        return sharedPreferences.getBoolean(PREF_USER_ANONYMOUS, true);
    }

    public void changeUserAnonymityStatus(boolean status) {
        sharedPreferences.edit()
            .putBoolean(PREF_USER_ANONYMOUS, status)
            .apply();
    }

    public void saveUserDetails(Profile profile) {
        sharedPreferences.edit()
            .putString(PREF_USER_PROFILE, profile.createJsonData())
            .apply();
    }

    public boolean isUserDetailsSaved(){
        return sharedPreferences.getString(PREF_USER_PROFILE, null) != null;
    }

    @Nullable
    public Profile getUserProfile() {
        try {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(PREF_USER_PROFILE, null);
            if (json != null) {
                return gson.fromJson(json, Profile.class);
            } else {
                return new Profile();
            }
        } catch (JsonSyntaxException e) {
            Timber.e(e);
            return null;
        }
    }

    public void saveUserSessionId(String sessionId) {
        sharedPreferences.edit()
            .putString(PREF_USER_SESSION_ID, sessionId)
            .apply();
    }

    public void saveUserEmail(String email) {
        sharedPreferences.edit()
            .putString(PREF_USER_EMAIL, email)
            .apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(PREF_USER_EMAIL, "");
    }

    public void saveUserPassword(String password) {
        sharedPreferences.edit()
            .putString(PREF_USER_PASSWORD, password)
            .apply();
    }

    public String getUserPassword() {
        return sharedPreferences.getString(PREF_USER_PASSWORD, "");
    }

    public void saveUserLastReportImport(String lastImportDate) {
        sharedPreferences.edit()
            .putString(PREF_USER_LAST_IMPORT_DATE, lastImportDate)
            .apply();
    }

    public String getUserLastReportImport() {
        return sharedPreferences.getString(PREF_USER_LAST_IMPORT_DATE, null);
    }

    public void changeUserRememberMeStatus(boolean rememberMeStatus) {
        sharedPreferences.edit()
            .putBoolean(PREF_USER_REMEMBER_ME_STATUS, rememberMeStatus)
            .apply();
    }

    public boolean getUserRememberMeStatus() {
        return sharedPreferences.getBoolean(PREF_USER_REMEMBER_ME_STATUS, true);
    }
}
