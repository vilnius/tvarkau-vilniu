package lt.vilnius.tvarkau;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import lt.vilnius.tvarkau.fragments.MyProfileFragment;

public class MyProfileActivity extends BaseActivity {

    private MyProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);

        if (savedInstanceState == null) {
            profileFragment = MyProfileFragment.getInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_frame, profileFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (profileFragment != null && !profileFragment.isEditedByUser()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.discard_changes_title))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.discard_changes_positive, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            MyProfileActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.discard_changes_negative, null).show();
        } else {
            super.onBackPressed();
        }
    }
}
