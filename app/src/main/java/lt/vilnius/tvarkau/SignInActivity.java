package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;


/*
    Activity which handles log in of user.
    It does not matter whether user decides to do it,
    he will be transferred to MainActivity

 */
public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int RC_SIGN_IN = 9001;


    private GoogleApiClient mGoogleApiClient;

    //Should be true only if user has entered app as anonymous

    @OnClick(R.id.login_tv_sign_in)
    public void handleGoogleSignIn(View view){
        Toast.makeText(this, "Handling login with google", Toast.LENGTH_SHORT).show();
        signIn();
    }

    @OnClick(R.id.login_tv_annon_enter)
    public void handleAnonymousUser(View view){
        Toast.makeText(this, "Handling login anonymously", Toast.LENGTH_SHORT).show();
        SharedPrefsManager.instance(this).setUserAnonymous(true);
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        ButterKnife.bind(this);

        //make sure that application will try to login.

        //Configure google sign in to request user email. Is ID and photo is included in Default Sign In.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Build googleApiClient with access to Sign In API and options specified in gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from account select intent.
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    protected void handleSignInResult(GoogleSignInResult result) {
        Log.d(this.getPackageName(), "handleSignInResult: " + result.isSuccess());
        if(result.isSuccess()){
            SharedPrefsManager.instance(this).setUserAnonymous(false);
            Intent intent = new Intent(this, MainActivity.class);
            //Clearing backstack after login. User does not need to go back to SignInActivity.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
