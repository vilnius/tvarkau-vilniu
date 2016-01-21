package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ButterKnife.bind(this);
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

    @OnClick(R.id.home_map_of_problems)
    protected void onProblemsMapClicked() {
        startNewActivity(ProblemsMapActivity.class);
    }

    @OnClick(R.id.home_my_profile)
    protected void onMyProfileClicked() {
        startNewActivity(MyProfileActivity.class);
    }
}
