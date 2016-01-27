package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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

        setContentView(R.layout.profile_activity);

        if (SharedPrefsManager.instance(this).getIsUserAnonymous()) {
            Toast.makeText(this, "anonymous profile not developed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_frame, MyProfileFragment.getInstance())
                    .commit();
        }

    }


}
