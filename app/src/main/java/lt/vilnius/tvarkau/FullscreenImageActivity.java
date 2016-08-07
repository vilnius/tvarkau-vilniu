package lt.vilnius.tvarkau;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.viewpagerindicator.CirclePageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter;

public class FullscreenImageActivity extends BaseActivity {

    public final static String EXTRA_IMAGE_POSITION = "FullscreenImageActivity.position";
    public final static String EXTRA_PHOTOS = "FullscreenImageActivity.photos";

    @BindView(R.id.problem_images_view_pager)
    ViewPager problemImagesViewPager;
    @BindView(R.id.problem_images_view_pager_indicator)
    CirclePageIndicator problemImagesViewPagerIndicator;

    private int initialImagePosition;
    private String[] photos;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            initialImagePosition = getIntent().getExtras().getInt(EXTRA_IMAGE_POSITION);
            photos = getIntent().getExtras().getStringArray(EXTRA_PHOTOS);
        }

        initializePager();
    }

    private void initializePager(){
        problemImagesViewPager.setAdapter(new ProblemImagesPagerAdapter<>(this, photos,
            R.layout.problem_fullscreen_image_view_pager_item));
        problemImagesViewPager.setOffscreenPageLimit(3);
        problemImagesViewPager.setCurrentItem(initialImagePosition);
        problemImagesViewPagerIndicator.setViewPager(problemImagesViewPager);
        problemImagesViewPagerIndicator.setVisibility(View.GONE);
        if (photos.length > 1) {
            problemImagesViewPagerIndicator.setVisibility(View.VISIBLE);
        }
    }
}
