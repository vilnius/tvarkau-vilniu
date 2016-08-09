package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import lt.vilnius.tvarkau.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FullscreenImagesPagerAdapter<T> extends PagerAdapter implements PhotoViewAttacher.OnViewTapListener {

    private LayoutInflater layoutInflater;
    private T[] images;
    private Context context;
    private View.OnClickListener onClickListener;

    public FullscreenImagesPagerAdapter(
        Context context, @Nullable T[] imageUrls, View.OnClickListener onClickListener) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = imageUrls;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return (images != null && images.length > 0) ? images.length : 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView;

        if (images != null && images.length > 0) {
            itemView = layoutInflater.inflate(R.layout.problem_fullscreen_image_view_pager_item, container, false);
            ImageView problemImageView = (ImageView) itemView.findViewById(R.id.problem_image_view);
            Glide.with(context).load(images[position]).into(problemImageView);

            // Enable image zoom for fullscreen images
            PhotoViewAttacher attacher = new PhotoViewAttacher(problemImageView);
            attacher.setOnViewTapListener(this);

        } else {
            itemView = layoutInflater.inflate(R.layout.no_image, container, false);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        onClickListener.onClick(view);
    }
}