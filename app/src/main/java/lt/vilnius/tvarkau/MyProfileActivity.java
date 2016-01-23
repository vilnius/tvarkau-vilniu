package lt.vilnius.tvarkau;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import lt.vilnius.tvarkau.fragments.MyProfileFragment;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

/**
 * An activity representing a single Problem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ProblemsListActivity}.
 */
public class MyProfileActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedPrefsManager.instance(this).getIsUserAnonymous()){
            showSignInFailed();
        }
        //User has logged in as anonymous user, lets open sign in screen for him

        setContentView(R.layout.profile_activity);

    }

    //If we were unable to sign user in, lets show him signInActivity
    @Override
    protected void handleSignInResult(GoogleSignInResult result) {
        Log.d(getClass().getSimpleName(), "Google sign in successful " + result.isSuccess());
        if(result.isSuccess()){
                mGoogleSignInAccount = result.getSignInAccount();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.profile_frame, MyProfileFragment.getInstance())
                        .commit();
        }else{
           showSignInFailed();
        }
    }




}
