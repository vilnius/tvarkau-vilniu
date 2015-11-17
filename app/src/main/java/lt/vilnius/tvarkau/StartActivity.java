package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViewById(R.id.report_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Replace with kotterknife
                startActivity(new Intent(StartActivity.this, ProblemListActivity.class));
            }
        });
        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);
    }
}