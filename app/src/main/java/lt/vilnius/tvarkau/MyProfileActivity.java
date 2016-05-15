package lt.vilnius.tvarkau;

import android.os.Bundle;

import lt.vilnius.tvarkau.fragments.MyProfileFragment;

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
