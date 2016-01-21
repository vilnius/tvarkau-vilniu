package lt.vilnius.tvarkau;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import icepick.Icepick;

/**
 * Created by Karolis Vycius on 2016-01-15.
 */

/*
    This class also is responsible for keeping user google account signed in. It is very important, that all activities, that
    will be using google sign in, would extend this class.

 */
public abstract class BaseActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener {


    protected GoogleApiClient mGoogleApiClient;
    protected GoogleSignInAccount mGoogleSignInAccount;

    private ProgressDialog mProgressDialog;


    //This means how we will draw our main menu.
    protected boolean isLoginAnon = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);


        //Configure google sign in to request user email. Is ID and photo is included in Default Sign In.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Build googleApiClient with access to Sign In API and options specified in gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }


    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone()){
            //User sign in is cached and available we can proceed directly to handling Sign In
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            showProgressDialog();
            //trying to log in silently.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });

        }
    }


    protected void handleSignInResult(GoogleSignInResult result) {
        Log.d(this.getPackageName(), "handleSignInResult: " + result.isSuccess());
        if(result.isSuccess()){
            GoogleSignInAccount mGoogleSignInAccount = result.getSignInAccount();

            Log.d(this.getPackageName(), "signedInUserName: " + mGoogleSignInAccount.getDisplayName());
            isLoginAnon = false;
        }else {
            startActivity(new Intent(this, SignInActivity.class));
        }

    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(this.getPackageName(), "onConnectionFailed:" + connectionResult);
    }


    protected void showProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.progress_dialog_loading));
            mProgressDialog.setIndeterminate(true);
        }
    }
    protected void hideProgressDialog() {
        if(mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }
}
