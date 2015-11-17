package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    @Bind(R.id.appbar) Toolbar appbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        setSupportActionBar(appbar);
    }

    @OnClick(R.id.report_list)
    public void onOpenProblemsListClicked() {
        startActivity(new Intent(this, ProblemListActivity.class));
    }
}