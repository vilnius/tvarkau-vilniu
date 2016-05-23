package lt.vilnius.tvarkau.entity;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import lt.vilnius.tvarkau.utils.SharedPrefsManager;


public class Profile {

    @SerializedName("Profile_Name")
    private String name;
    @SerializedName("Profile_Email")
    private String email;
    @SerializedName("Profile_Mobile_Phone")
    private String mobilePhone;

    @SerializedName("Profile_picture_URL")
    private String pictureUrl;

    public Profile() {
    }

    public Profile(GoogleSignInAccount acct) {
        name = acct.getDisplayName();
        email = acct.getEmail();
        pictureUrl = acct.getPhotoUrl().toString();

    }

    public Profile(String name, String email, String mobilePhone) {
        this.name = name;
        this.email = email;
        this.mobilePhone = mobilePhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public static Profile returnProfile(Context con) {
        return SharedPrefsManager.getInstance(con).getUserProfile();
    }

    public String createJsonData() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return createJsonData();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Profile) {
            Profile profile = (Profile) o;

            return name.equals(profile.getName()) &&
                    email.equals(profile.getEmail()) &&
                    mobilePhone.equals(profile.getMobilePhone());
        }

        return false;
    }
}
