package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.fragments.ProblemsListFragment;

/**
 * Created by Karolis Vycius on 2016-01-30.
 */
public class ProblemsListViewPagerAdapter extends FragmentPagerAdapter {

    protected CharSequence[] titles;

    public ProblemsListViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        titles = context.getResources().getTextArray(R.array.problems_list_tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        // TODO change to real fragments
        if (position == 0) {
            return ProblemsListFragment.getInstance();
        } else {
            return ProblemsListFragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
