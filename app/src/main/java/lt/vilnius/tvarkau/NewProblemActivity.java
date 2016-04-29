package lt.vilnius.tvarkau;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NewProblemActivity extends BaseActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 10;
    public static final int PLACE_PICKER_REQUEST = 11;
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
    Spinner mReportProblemType;
    @Bind(R.id.report_problem_privacy_mode)
    Spinner mReportProblemPrivacyMode;
    @Bind(R.id.report_problem_description)
    EditText mReportProblemDescription;

    @State
    File lastPhotoFile;
    @State
    LatLng locationCords;
    @State
    ArrayList<Uri> problemImagesURIs = new ArrayList<>();

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
        Intent intent = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @OnClick(R.id.report_problem_take_photo)
    public void onTakePhotoClicked() {
        if (PermissionUtils.isAllPermissionsGranted(this, REQUIRED_PERMISSIONS)) {
            takePhoto();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
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
                mReportProblemType.getSelectedItemPosition() > 0 ||
                locationCords != null || problemImagesURIs.size() > 0;
    }

    @Override
    public void onBackPressed() {
        if (isEditedByUser()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.discardChanges))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            NewProblemActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
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
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    mAddProblemLocation.setText(place.getName());
                    locationCords = place.getLatLng();
                    break;
            }
        }
    }


    private void setPhotos(Uri[] photoUris) {
        mProblemImagesViewPager.setAdapter(new ProblemImagesPagerAdapter<Uri>(this, photoUris) {
            @Override
            public void loadImage(Uri imageURI, Context context, ImageView imageView) {
                Picasso.with(context).load(new File(imageURI.getPath())).into(imageView);
            }
        });
    }

    @OnClick(R.id.report_problem_location)
    public void onProblemLocationClicked(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(this);
            Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();

            ActivityCompat.startActivityForResult(this, intent, PLACE_PICKER_REQUEST, bundle);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Check Google Play Services!", Toast.LENGTH_LONG).show();
        }
    }
}
