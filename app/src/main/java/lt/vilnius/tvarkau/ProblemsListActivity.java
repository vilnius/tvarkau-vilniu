package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.views.adapters.ProblemsListViewPagerAdapter;

/**
 * An activity representing a list of Problems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProblemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ProblemsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.problems_list_tab_layout)
    TabLayout tabLayout;

    @Bind(R.id.problems_list_view_pager)
    ViewPager viewPager;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problems_list_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        setTabs();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setTabs() {
        viewPager.setAdapter(new ProblemsListViewPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @OnClick(R.id.fab)
    public void onNewProblemClicked(View view) {
        startActivityForResult(new Intent(this, NewProblemActivity.class), 0);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "Searched for: " + query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
