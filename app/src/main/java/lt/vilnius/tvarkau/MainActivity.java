package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.GlobalConsts;

import static lt.vilnius.tvarkau.ProblemsListActivity.ALL_PROBLEMS;
import static lt.vilnius.tvarkau.ProblemsListActivity.MY_PROBLEMS;


/**
 * An activity representing a main activity home screen
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
    }

    protected void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);

        startActivity(intent);
    }

    protected void startNewActivity(Class<?> cls, Bundle data) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(data);

        startActivity(intent);
    }

    @OnClick(R.id.home_report_problem)
    protected void onNewIssueClicked() {
        startNewActivity(NewProblemActivity.class);
    }

    @OnClick(R.id.home_list_of_problems)
    protected void onProblemsListClicked() {
        Intent intent = ProblemsListActivity.getStartActivityIntent(this, ALL_PROBLEMS);

        startActivity(intent);
    }

    @OnClick(R.id.home_my_problems)
    protected void onMyProblemsClicked() {
        Intent intent = ProblemsListActivity.getStartActivityIntent(this, MY_PROBLEMS);

        startActivity(intent);
    }

    @OnClick(R.id.home_map_of_problems)
    protected void onProblemsMapClicked() {
        Bundle data = new Bundle();
        data.putString(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT);
        startNewActivity(ProblemsMapActivity.class, data);
    }

    @OnClick(R.id.home_my_profile)
    protected void onMyProfileClicked() {
        startNewActivity(MyProfileActivity.class);
    }

    @OnClick(R.id.home_about)
    protected void onAboutClicked() { startNewActivity(AboutActivity.class); }
}
