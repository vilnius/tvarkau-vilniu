package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    // TODO create my profile activity
    @OnClick(R.id.home_my_profile)
    protected void onMyProfileClicked() {
        Toast.makeText(this, "My profile is not implemented", Toast.LENGTH_SHORT).show();
    }
}
