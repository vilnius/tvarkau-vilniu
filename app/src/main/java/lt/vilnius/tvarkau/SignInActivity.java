package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.utils.GoogleSignInHelper;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;


/*
    Activity which handles log in of user.
    It does not matter whether user decides to do it,
    he will be transferred to MainActivity

 */
public class SignInActivity extends AppCompatActivity implements GoogleSignInHelper.GooglePlusSignInInterface {


    private GoogleSignInHelper googleSignInHelper;
    private SharedPrefsManager prefsManager;

    @OnClick(R.id.login_tv_sign_in)
    public void handleGoogleSignIn(View view) {
        Toast.makeText(this, "Handling login with google", Toast.LENGTH_SHORT).show();
        googleSignInHelper.signIn();
    }

    @OnClick(R.id.login_tv_annon_enter)
    public void handleAnonymousUser(View view) {
        Toast.makeText(this, "Handling login anonymously", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        ButterKnife.bind(this);

        googleSignInHelper = new GoogleSignInHelper(this, this);
        prefsManager = SharedPrefsManager.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleSignInHelper.connect();
    }

    @Override
    protected void onStop() {
        googleSignInHelper.disconnect();
        super.onStop();
    }


    private void saveUserDetailsInPrefs(GoogleSignInResult result) {
        prefsManager.saveUserDetails(new Profile(result.getSignInAccount()));
        Intent intent = new Intent(this, MainActivity.class);
        // Clearing backstack after login. User does not need to go back to SignInActivity.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /*
        Result requested from GoogleSignInHelper class. onActivityResult should not be used in any other classes that are using google sign in, as it is used only for authentication
        If any other class will have to use google sign in result, it will have to use
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from account select intent.
        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleSignInHelper.checkSignInResult(result);
        }
    }


    @Override
    public void startGoogleActivityForResult() {
        startActivityForResult(googleSignInHelper.getSignInIntent(), GoogleSignInHelper.RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
    }

    // Should not happen in this activity.
    @Override
    public void onAuthenticationFailed(GoogleSignInResult result) {
        Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSuccessful(GoogleSignInResult signInResult) {
        saveUserDetailsInPrefs(signInResult);
    }
}
