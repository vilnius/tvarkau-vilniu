package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.GoogleSignInHelper;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;


/*
    Activity which handles log in of user.
    It does not matter whether user decides to do it,
    he will be transferred to MainActivity

 */
public class SignInActivity extends AppCompatActivity {


    private GoogleSignInHelper googleSignInHelper;

    @OnClick(R.id.login_tv_sign_in)
    public void handleGoogleSignIn(View view) {
        Toast.makeText(this, "Handling login with google", Toast.LENGTH_SHORT).show();
        googleSignInHelper.authenticateUser();
    }

    @OnClick(R.id.login_tv_annon_enter)
    public void handleAnonymousUser(View view) {
        Toast.makeText(this, "Handling login anonymously", Toast.LENGTH_SHORT).show();
        SharedPrefsManager.instance(this).setUserAnonymous(true);
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        ButterKnife.bind(this);

        googleSignInHelper = new GoogleSignInHelper(this);
    }

    //Result requested from GoogleSignInHelper class.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from account select intent.
        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleSignInHelper.saveUserDetailsInPrefs(result);
        }
    }


}
