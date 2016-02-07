package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * An activity representing a main activity home screen
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
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

    @OnClick(R.id.home_report_problem)
    protected void onNewIssueClicked() {
        startNewActivity(NewProblemActivity.class);
    }

    @OnClick(R.id.home_list_of_problems)
    protected void onProblemsListClicked() {
        startNewActivity(ProblemsListActivity.class);
    }

    @OnClick(R.id.home_my_problems)
    protected void onMyProblemsClicked() {
        Toast.makeText(this, "This should be properly implemented", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.home_map_of_problems)
    protected void onProblemsMapClicked() {
        startNewActivity(ProblemsMapActivity.class);
    }

    @OnClick(R.id.home_my_profile)
    protected void onMyProfileClicked() {
        startNewActivity(MyProfileActivity.class);
    }

    @OnClick(R.id.home_about)
    protected void onAboutClicked() {
        Toast.makeText(this, "This should be implemented", Toast.LENGTH_SHORT).show();
    }

}
