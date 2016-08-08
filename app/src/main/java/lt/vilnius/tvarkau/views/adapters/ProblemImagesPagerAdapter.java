package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import lt.vilnius.tvarkau.FullscreenImageActivity;
import lt.vilnius.tvarkau.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Gediminas Zukas on 2016-04-28.
 */
public class ProblemImagesPagerAdapter<T> extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private T[] images;
    private Context context;
    @LayoutRes private int layoutRes;

    public ProblemImagesPagerAdapter(Context context, @Nullable T[] imageUrls, @LayoutRes int layoutRes) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = imageUrls;
        this.context = context;
        this.layoutRes = layoutRes;
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
            itemView = layoutInflater.inflate(layoutRes, container, false);
            ImageView problemImageView = (ImageView) itemView.findViewById(R.id.problem_image_view);
            Glide.with(context).load(images[position]).into(problemImageView);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullscreenImageActivity.class);
                intent.putExtra(FullscreenImageActivity.EXTRA_PHOTOS, images);
                intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_POSITION, position);
                context.startActivity(intent);
            });

            // Enable image zoom for fullscreen images
            if (layoutRes == R.layout.problem_fullscreen_image_view_pager_item) {
                PhotoViewAttacher attacher = new PhotoViewAttacher(problemImageView);
            }

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
}
