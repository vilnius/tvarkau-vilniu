package lt.vilnius.tvarkau;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.GlobalConsts;
import lt.vilnius.tvarkau.views.adapters.ProblemsListViewPagerAdapter;

/**
 * An activity representing a list of Problems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProblemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ProblemsListActivity extends AppCompatActivity
        implements MenuItem.OnMenuItemClickListener {

    public static final int ALL_PROBLEMS = 0;
    public static final int MY_PROBLEMS = 1;

    @IntDef({ALL_PROBLEMS, MY_PROBLEMS})
    public @interface ProblemsTabsInitialPosition {
    }

    protected static final String EXTRA_INITIAL_POSITION = "list.initial_position";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.problems_list_tab_layout)
    TabLayout tabLayout;

    @Bind(R.id.problems_list_view_pager)
    ViewPager viewPager;

    private int initialPosition;


//    SearchView searchView;

    public static Intent getStartActivityIntent(Context context, @ProblemsTabsInitialPosition int initialPosition) {
        Intent intent = new Intent(context, ProblemsListActivity.class);

        intent.putExtra(EXTRA_INITIAL_POSITION, initialPosition);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problems_list_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if(getIntent().getExtras() != null) {
            initialPosition = getIntent().getExtras().getInt(EXTRA_INITIAL_POSITION, ALL_PROBLEMS);
        }

        setTabs();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setTabs() {
        if(viewPager.getAdapter() == null) {
            viewPager.setAdapter(new ProblemsListViewPagerAdapter(this, getSupportFragmentManager()));
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(initialPosition);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);

//        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        searchView.setOnQueryTextListener(this);

        menu.findItem(R.id.action_map).setOnMenuItemClickListener(this);
        return true;
    }

    @OnClick(R.id.fab_report_problem)
    public void onNewProblemClicked(View view) {
        Intent intent = new Intent(this, NewProblemActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0,
                view.getWidth(), view.getHeight()).toBundle();

        ActivityCompat.startActivity(this, intent, bundle);
    }


//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        Toast.makeText(this, "Searched for: " + query, Toast.LENGTH_SHORT).show();
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        return false;
//    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_map) {
            Intent intent = new Intent(this, ProblemsMapActivity.class);

            intent.putExtra(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT);
            startActivity(intent);

            return true;
        }
        return false;
    }
}
