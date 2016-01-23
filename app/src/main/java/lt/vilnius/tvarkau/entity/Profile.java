package lt.vilnius.tvarkau.entity;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by vn on 16.1.21.
 */
public class Profile {

    GoogleSignInAccount mGoogleSignInAccount;

    private String name;
    private String email;
    private String mobilePhone;

    private Uri pictureUrl;

    public Profile(){

    }

    public Profile(GoogleSignInAccount acct){
        mGoogleSignInAccount = acct;

        name = acct.getDisplayName();
        email = acct.getEmail();
        pictureUrl = acct.getPhotoUrl();


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
    public Uri getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(Uri pictureUrl) {
        this.pictureUrl = pictureUrl;
    }


}
