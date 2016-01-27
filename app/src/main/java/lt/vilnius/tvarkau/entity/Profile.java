package lt.vilnius.tvarkau.entity;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import lt.vilnius.tvarkau.utils.SharedPrefsManager;

/**
 * Created by vn on 16.1.21.
 */
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public static Profile returnProfile(Context con) {
        return SharedPrefsManager.instance(con).getUserProfile();
    }

    public String createJsonData(Profile profile) {
        Gson gson = new Gson();
        return gson.toJson(profile);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }


}
