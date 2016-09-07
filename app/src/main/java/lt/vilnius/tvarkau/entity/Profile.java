package lt.vilnius.tvarkau.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;

import lt.vilnius.tvarkau.utils.SharedPrefsManager;


public class Profile implements Parcelable {

    @SerializedName("Profile_Name")
    private String name;
    @SerializedName("Profile_Birthday")
    private LocalDate birthday;
    @SerializedName("Profile_Email")
    private String email;
    @SerializedName("Profile_Mobile_Phone")
    private String mobilePhone;

    public Profile() {
    }

    public Profile(String name, LocalDate birthday, String email, String mobilePhone) {
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.mobilePhone = mobilePhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
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
                birthday.equals(profile.getBirthday()) &&
                email.equals(profile.getEmail()) &&
                mobilePhone.equals(profile.getMobilePhone());
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.mobilePhone);
    }

    protected Profile(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.mobilePhone = in.readString();
    }

    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
