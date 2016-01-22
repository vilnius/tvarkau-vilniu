package lt.vilnius.tvarkau;

import android.os.Bundle;

import lt.vilnius.tvarkau.fragments.MyProfileFragment;

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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_frame, MyProfileFragment.getInstance())
                .commit();
    }
}
