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
public abstract class ProblemImagesPagerAdapter<T> extends PagerAdapter {

    private LayoutInflater mLayoutInflater;
    private T[] mResources;

    public ProblemImagesPagerAdapter(Context context, T[] images) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = images;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public abstract void loadImage(T resource, Context context, ImageView imageView);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO: consider recycling views
        View itemView = mLayoutInflater.inflate(R.layout.problem_images_view_pager_item, container, false);

        ImageView problemImageView = (ImageView) itemView.findViewById(R.id.problem_image_view);

        loadImage(mResources[position], container.getContext(), problemImageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
