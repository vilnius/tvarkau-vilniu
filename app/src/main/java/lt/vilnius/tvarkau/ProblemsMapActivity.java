package lt.vilnius.tvarkau;

import android.os.Bundle;

import lt.vilnius.tvarkau.fragments.ProblemsMapFragment;

public class ProblemsMapActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.problems_map_activity);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.problems_map_frame, ProblemsMapFragment.getInstance())
                .commit();
    }
}
