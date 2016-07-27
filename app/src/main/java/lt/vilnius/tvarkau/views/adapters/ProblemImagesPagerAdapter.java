package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


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

    public static ProblemImagesPagerAdapter<Void> empty(Context context) {
        return new ProblemImagesPagerAdapter<Void>(context, new Void[0]) {

            @Override
            public void loadImage(Void resource, Context context, ImageView imageView) {

            }
        };
    }

    @Override
    public int getCount() {
        return (mResources != null && mResources.length > 0) ? mResources.length : 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public abstract void loadImage(T resource, Context context, ImageView imageView);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView;

        if (mResources != null && mResources.length > 0) {
            // TODO: consider recycling views
            itemView = mLayoutInflater.inflate(R.layout.problem_images_view_pager_item, container, false);

            ImageView problemImageView = (ImageView) itemView.findViewById(R.id.problem_image_view);

            loadImage(mResources[position], container.getContext(), problemImageView);
        } else {
            itemView = mLayoutInflater.inflate(R.layout.no_image, container, false);
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
