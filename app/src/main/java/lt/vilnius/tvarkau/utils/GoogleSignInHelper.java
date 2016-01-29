package lt.vilnius.tvarkau.utils;

import android.content.Context;
import android.content.Intent;
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
    private Context context;

    // Parent activity has to call startActivityForResult() method to launch connection intent by itself.
    public interface GooglePlusSignInInterface {
        void startGoogleActivityForResult();
    }

    GooglePlusSignInInterface signInInterface;

    public void setSignInInterface(GooglePlusSignInInterface signInInterface) {
        this.signInInterface = signInInterface;
    }


    public GoogleSignInHelper(Context context) {
        this.context = context;

        //Configure google sign in to request user email. Is ID and photo is included in Default Sign In.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Build googleApiClient with access to Sign In API and options specified in gso.
        googleApiClient = new GoogleApiClient.Builder(context).addOnConnectionFailedListener(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

    }


    public void signIn() {
        signInInterface.startGoogleActivityForResult();
    }


    public void saveUserDetailsInPrefs(GoogleSignInResult result) {
        if (result.isSuccess()) {
            SharedPrefsManager.initializeInstance(context.getApplicationContext());
            SharedPrefsManager.saveUserDetails(new Profile(result.getSignInAccount()));
            Intent intent = new Intent(context, MainActivity.class);
            //Clearing backstack after login. User does not need to go back to SignInActivity.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
    }

    public void Connect() {
        googleApiClient.connect();
    }

    public void Disconnect() {
        googleApiClient.disconnect();
    }

    public Intent getSignInIntent() {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }


}
