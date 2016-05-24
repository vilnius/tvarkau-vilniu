package lt.vilnius.tvarkau.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;


public class GoogleSignInHelper implements GoogleApiClient.OnConnectionFailedListener {
    public static final int RC_SIGN_IN = 9001;

    private GoogleApiClient googleApiClient;

    private GooglePlusSignInInterface signInInterface;


    // Parent activity has to call startActivityForResult() method to launch connection intent by itself.
    public interface GooglePlusSignInInterface {
        void startGoogleActivityForResult();

        void onConnectionFailed(ConnectionResult connectionResult);

        void onAuthenticationFailed(GoogleSignInResult result);

        void onAuthenticationSuccessful(GoogleSignInResult signInResult);
    }


    public GoogleSignInHelper(Context context, GooglePlusSignInInterface signInInterface) {
        this.signInInterface = signInInterface;

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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        signInInterface.onConnectionFailed(connectionResult);
    }

    public void onAuthenticationSuccessful(GoogleSignInResult data) {
        signInInterface.onAuthenticationSuccessful(data);
    }

    public void connect() {
        googleApiClient.connect();
    }

    public void disconnect() {
        googleApiClient.disconnect();
    }

    public Intent getSignInIntent() {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }

    /*
        Silent log in which should be used my all other classes that are using google sign in api. If returned false,
        silent log in has failed, and user should be returned back to sign in activity.
     */
    private void trySilentLogIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.

            GoogleSignInResult result = opr.get();
            checkSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(this::checkSignInResult);
        }
    }

    public void checkSignInResult(GoogleSignInResult signInResult) {
        if (signInResult.isSuccess()) {
            signInInterface.onAuthenticationSuccessful(signInResult);
        } else {
            signInInterface.onAuthenticationFailed(signInResult);
        }
    }


}
