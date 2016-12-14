package lt.vilnius.tvarkau.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;


public class Profile {

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

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
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
}
