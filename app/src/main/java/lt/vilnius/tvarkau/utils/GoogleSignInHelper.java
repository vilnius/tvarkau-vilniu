package lt.vilnius.tvarkau.utils;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import lt.vilnius.tvarkau.MainActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Profile;


public class GoogleSignInHelper implements GoogleApiClient.OnConnectionFailedListener {
    public static final int RC_SIGN_IN = 9001;

    private GoogleApiClient googleApiClient;

    private FragmentActivity activity;


    public GoogleSignInHelper(FragmentActivity activity) {
        this.activity = activity;

        //make sure that application will try to login.

        //Configure google sign in to request user email. Is ID and photo is included in Default Sign In.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Build googleApiClient with access to Sign In API and options specified in gso.
        //Is passing activity is really healthy???
        googleApiClient = new GoogleApiClient.Builder(activity).enableAutoManage(activity, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    public void authenticateUser() {
        signIn();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void saveUserDetailsInPrefs(GoogleSignInResult result) {
        if (result.isSuccess()) {
            SharedPrefsManager.instance(activity).setUserAnonymous(false);
            SharedPrefsManager.instance(activity).saveUserDetails(new Profile(result.getSignInAccount()));
            Intent intent = new Intent(activity, MainActivity.class);
            //Clearing backstack after login. User does not need to go back to SignInActivity.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(activity, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
    }


}
