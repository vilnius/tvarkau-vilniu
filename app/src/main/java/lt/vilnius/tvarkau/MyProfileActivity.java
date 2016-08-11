package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.fragments.MyProfileFragment;
import lt.vilnius.tvarkau.utils.GoogleSignInHelper;
import lt.vilnius.tvarkau.utils.GoogleSignInHelper.GooglePlusSignInInterface;
import lt.vilnius.tvarkau.utils.KeyboardUtils;

import static com.google.android.gms.common.SignInButton.COLOR_DARK;
import static com.google.android.gms.common.SignInButton.SIZE_WIDE;
import static lt.vilnius.tvarkau.utils.GoogleSignInHelper.RC_SIGN_IN;

public class MyProfileActivity extends BaseActivity implements GooglePlusSignInInterface {

    private MyProfileFragment profileFragment;
    private GoogleSignInHelper googleSignInHelper;

    @BindView(R.id.google_sign_in_button)
    SignInButton signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);

        if (savedInstanceState == null) {
            profileFragment = MyProfileFragment.getInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_frame, profileFragment)
                    .commit();

            googleSignInHelper = new GoogleSignInHelper(this, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        ButterKnife.bind(this);

        signInButton.setColorScheme(COLOR_DARK);
        signInButton.setSize(SIZE_WIDE);

        googleSignInHelper.connect();
    }

    @Override
    protected void onStop() {
        googleSignInHelper.disconnect();

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleSignInHelper.checkSignInResult(result);
        }
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            KeyboardUtils.closeSoftKeyboard(this, view);
        }
        if (profileFragment != null && !profileFragment.isEditedByUser()) {
            new AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setMessage(getString(R.string.discard_changes_title))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.discard_changes_positive, (dialog, whichButton) ->
                            MyProfileActivity.super.onBackPressed())
                    .setNegativeButton(R.string.discard_changes_negative, null).show();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.google_sign_in_button)
    public void handleGoogleSignIn() {
        googleSignInHelper.signIn();
    }

    @Override
    public void startGoogleActivityForResult() {
        startActivityForResult(googleSignInHelper.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed(GoogleSignInResult result) {
        Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSuccessful(GoogleSignInResult signInResult) {
        if (profileFragment != null)
            profileFragment.fillFields(signInResult.getSignInAccount());
    }
}
