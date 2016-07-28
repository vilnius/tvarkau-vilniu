package lt.vilnius.tvarkau;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import lt.vilnius.tvarkau.fragments.ProblemDetailFragment;

/**
 * An activity representing a single Problem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ProblemsListActivity}.
 */
public class ProblemDetailActivity extends AppCompatActivity {

    public static Intent getStartActivityIntent(Context context, String problemId) {
        Intent intent = new Intent(context, ProblemDetailActivity.class);
        intent.putExtra(ProblemDetailFragment.ARG_ITEM_ID, problemId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String problemId = getIntent().getStringExtra(ProblemDetailFragment.ARG_ITEM_ID);
            if (actionBar != null) {
                actionBar.setTitle(problemId);
            }
            ProblemDetailFragment fragment = ProblemDetailFragment.getInstance(problemId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.problem_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
