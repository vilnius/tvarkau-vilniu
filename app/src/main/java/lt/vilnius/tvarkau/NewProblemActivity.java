package lt.vilnius.tvarkau;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import icepick.State;
import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.entity.ReportType;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static lt.vilnius.tvarkau.ChooseReportTypeActivity.EXTRA_REPORT_TYPE;

public class NewProblemActivity extends BaseActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 10;

    public static final int REQUEST_PLACE_PICKER = 11;
    public static final int REQUEST_PROFILE = 12;
    public static final int REQUEST_CHOOSE_REPORT_TYPE = 13;


    private static final String[] REQUIRED_PERMISSIONS = {WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE};


    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.report_problem_location)
    EditText mAddProblemLocation;
    @Bind(R.id.problem_images_view_pager)
    ViewPager mProblemImagesViewPager;
    @Bind(R.id.problem_images_view_pager_indicator)
    CirclePageIndicator mProblemImagesViewPagerIndicator;
    @Bind(R.id.report_problem_type)
    EditText mReportProblemType;
    @Bind(R.id.report_problem_privacy_mode)
    Spinner mReportProblemPrivacyMode;
    @Bind(R.id.report_problem_description)
    EditText mReportProblemDescription;
    @Bind(R.id.report_problem_take_photo)
    FloatingActionButton mReportProblemTakePhoto;

    @State
    File lastPhotoFile;
    @State
    LatLng locationCords;
    @State
    ArrayList<Uri> problemImagesURIs;
    @State
    Profile profile;
    @State
    ReportType reportType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_problem);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initProblemImagesPager();
    }

    private void initProblemImagesPager() {
        mProblemImagesViewPager.setAdapter(ProblemImagesPagerAdapter.empty(this));
        mProblemImagesViewPager.setOffscreenPageLimit(3);
        mProblemImagesViewPagerIndicator.setViewPager(mProblemImagesViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_send:
                sendProblem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_problem_toolbar_menu, menu);

        return true;
    }


    public void sendProblem() {
        Toast.makeText(this, "Should implement send behaviour", Toast.LENGTH_SHORT).show();
    }

    public void takePhoto() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.choose_photos_title);

        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(this, ImagePickerActivity.class);

        Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(mReportProblemTakePhoto, 0, 0,
                mReportProblemTakePhoto.getWidth(), mReportProblemTakePhoto.getHeight()).toBundle();

        if (problemImagesURIs != null) {
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, problemImagesURIs);
        }

        ActivityCompat.startActivityForResult(this, intent, REQUEST_IMAGE_CAPTURE, bundle);
    }

    @OnClick(R.id.report_problem_take_photo)
    public void onTakePhotoClicked() {
        if (PermissionUtils.isAllPermissionsGranted(this, REQUIRED_PERMISSIONS)) {
            takePhoto();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    @OnClick(R.id.report_problem_type)
    public void onChooseProblemTypeClicked() {
        Intent intent = new Intent(this, ChooseReportTypeActivity.class);

        startActivityForResult(intent, REQUEST_CHOOSE_REPORT_TYPE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (PermissionUtils.isAllPermissionsGranted(this, REQUIRED_PERMISSIONS)) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "Need camera and storage permissions to take photos.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isEditedByUser() {
        return mReportProblemDescription.getText().length() > 0 ||
                mReportProblemPrivacyMode.getSelectedItemPosition() > 0 ||
                reportType != null || locationCords != null || problemImagesURIs != null;
    }

    @Override
    public void onBackPressed() {
        if (isEditedByUser()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.discard_changes_title))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.discard_changes_positive, (dialog, whichButton) ->
                            NewProblemActivity.super.onBackPressed())
                    .setNegativeButton(R.string.discard_changes_negative, null).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    problemImagesURIs = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                    Uri[] problemImagesURIsArr = problemImagesURIs.toArray(new Uri[problemImagesURIs.size()]);
                    setPhotos(problemImagesURIsArr);
                    break;
                case REQUEST_PLACE_PICKER:
                    Place place = PlacePicker.getPlace(this, data);
                    mAddProblemLocation.setText(place.getName());
                    locationCords = place.getLatLng();
                    break;
                case REQUEST_PROFILE:
                    profile = Profile.returnProfile(this);
                    break;
                case REQUEST_CHOOSE_REPORT_TYPE:
                    if (data.hasExtra(EXTRA_REPORT_TYPE)) {
                        reportType = data.getParcelableExtra(EXTRA_REPORT_TYPE);
                        mReportProblemType.setText(reportType.getName());
                    }
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_PROFILE:
                    mReportProblemPrivacyMode.setSelection(0);
                    break;
            }
        }
    }


    private void setPhotos(Uri[] photoUris) {
        mProblemImagesViewPager.setAdapter(new ProblemImagesPagerAdapter<Uri>(this, photoUris) {
            @Override
            public void loadImage(Uri imageURI, Context context, ImageView imageView) {
                Glide.with(context).load(new File(imageURI.getPath())).centerCrop().into(imageView);
            }
        });
    }

    @OnClick(R.id.report_problem_location)
    public void onProblemLocationClicked(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(this);
            Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();

            ActivityCompat.startActivityForResult(this, intent, REQUEST_PLACE_PICKER, bundle);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Check Google Play Services!", Toast.LENGTH_LONG).show();
        }
    }

    @OnItemSelected(R.id.report_problem_privacy_mode)
    public void onPrivacyModeSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                profile = null;
                break;
            case 1:
                profile = Profile.returnProfile(this);

                if (profile == null) {
                    Intent intent = new Intent(this, MyProfileActivity.class);
                    startActivityForResult(intent, REQUEST_PROFILE);
                }
                break;
        }
    }
}
