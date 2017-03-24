package lt.vilnius.tvarkau;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.views.adapters.FullscreenImagesPagerAdapter;
import lt.vilnius.tvarkau.views.adapters.HackyViewPager;

public class FullscreenImageActivity extends BaseActivity {

    public final static String EXTRA_IMAGE_POSITION = "FullscreenImageActivity.position";
    public final static String EXTRA_PHOTOS = "FullscreenImageActivity.photos";

    @BindView(R.id.problem_images_view_pager)
    HackyViewPager problemImagesViewPager;
    @BindView(R.id.fullscreen_layout)
    RelativeLayout fullscreenLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int initialImagePosition;
    private String[] photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().hide();
        }

        if (getIntent().getExtras() != null) {
            initialImagePosition = getIntent().getExtras().getInt(EXTRA_IMAGE_POSITION);
            photos = getIntent().getExtras().getStringArray(EXTRA_PHOTOS);
        }

        initializePager();
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

    private void initializePager() {
        problemImagesViewPager.setAdapter(new FullscreenImagesPagerAdapter<>(this, photos, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FullscreenImageActivity.this.getSupportActionBar() != null) {
                    if (FullscreenImageActivity.this.getSupportActionBar().isShowing()) {
                        FullscreenImageActivity.this.getSupportActionBar().hide();
                    } else {
                        FullscreenImageActivity.this.getSupportActionBar().show();
                    }
                }
            }
        }));
        problemImagesViewPager.setOffscreenPageLimit(3);
        problemImagesViewPager.setCurrentItem(initialImagePosition);
    }
}
