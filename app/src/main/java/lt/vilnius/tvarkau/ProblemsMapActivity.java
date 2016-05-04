package lt.vilnius.tvarkau;

import android.os.Bundle;

import lt.vilnius.tvarkau.fragments.BaseMapFragment;
import lt.vilnius.tvarkau.fragments.MultipleProblemsMapFragment;
import lt.vilnius.tvarkau.fragments.SingleProblemMapFragment;
import lt.vilnius.tvarkau.utils.GlobalConsts;

public class ProblemsMapActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.problems_map_activity);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            BaseMapFragment fragment;
            String fragmentTag = data.getString(GlobalConsts.KEY_MAP_FRAGMENT);

            if (fragmentTag != null && fragmentTag.equals(GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT))
                fragment = SingleProblemMapFragment.getInstance();
            else if (fragmentTag != null && fragmentTag.equals(GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT))
                fragment = MultipleProblemsMapFragment.getInstance();
            else
                return;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.problems_map_frame, fragment)
                    .commit();
        }
    }
}
