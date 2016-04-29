package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import lt.vilnius.tvarkau.R;

/**
 * Created by Gediminas Zukas on 2016-04-28.
 */
public class ProblemImagesPagerAdapter extends PagerAdapter {

    private LayoutInflater mLayoutInflater;
    private int[] mResources;

    public ProblemImagesPagerAdapter(Context context, int[] imagesIds) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = imagesIds;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO: consider recycling views
        View itemView = mLayoutInflater.inflate(R.layout.problem_images_view_pager_item, container, false);

        ImageView problemImageView = (ImageView) itemView.findViewById(R.id.problem_image_view);
        Picasso.with(container.getContext()).load(mResources[position]).into(problemImageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
