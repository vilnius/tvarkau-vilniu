package lt.vilnius.tvarkau;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.viewpagerindicator.CirclePageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.views.adapters.HackyViewPager;
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter;

public class FullscreenImageActivity extends BaseActivity {

    public final static String EXTRA_IMAGE_POSITION = "FullscreenImageActivity.position";
    public final static String EXTRA_PHOTOS = "FullscreenImageActivity.photos";

    @BindView(R.id.problem_images_view_pager)
    HackyViewPager problemImagesViewPager;
    @BindView(R.id.problem_images_view_pager_indicator)
    CirclePageIndicator problemImagesViewPagerIndicator;
    @BindView(R.id.fullscreen_layout)
    RelativeLayout fullscreenLayout;

    private int initialImagePosition;
    private String[] photos;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
        }

        setContentView(R.layout.activity_fullscreen);

        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            initialImagePosition = getIntent().getExtras().getInt(EXTRA_IMAGE_POSITION);
            photos = getIntent().getExtras().getStringArray(EXTRA_PHOTOS);
        }

        initializePager();
    }

    private void initializePager() {
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Need to recreate layout after phone rotation
        initialImagePosition = problemImagesViewPager.getCurrentItem();
        initializePager();
    }
}
