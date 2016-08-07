package lt.vilnius.tvarkau;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.crash.FirebaseCrash;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import icepick.State;
import lt.vilnius.tvarkau.api.ApiMethod;
import lt.vilnius.tvarkau.api.ApiRequest;
import lt.vilnius.tvarkau.api.ApiResponse;
import lt.vilnius.tvarkau.api.GetNewProblemParams;
import lt.vilnius.tvarkau.api.LegacyApiModule;
import lt.vilnius.tvarkau.api.LegacyApiService;
import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.entity.ReportType;
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent;
import lt.vilnius.tvarkau.utils.GlobalConsts;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static lt.vilnius.tvarkau.ChooseReportTypeActivity.EXTRA_REPORT_TYPE;

@AutoComponent(modules = {LegacyApiModule.class, AppModule.class, SharedPreferencesModule.class})
@AutoInjector
@Singleton
public class NewProblemActivity extends BaseActivity {

    @Inject LegacyApiService legacyApiService;
    @Inject SharedPreferences myProblemsPreferences;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 10;

    public static final int REQUEST_PLACE_PICKER = 11;
    public static final int REQUEST_PROFILE = 12;
    public static final int REQUEST_CHOOSE_REPORT_TYPE = 13;

    private static final String[] REQUIRED_PERMISSIONS = {WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE};

    private static final String ANONYMOUS_USER_SESSION_IS = "null";
    private static final String PROBLEM_PREFERENCE_KEY = "problem";


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.report_problem_location)
    EditText mAddProblemLocation;
    @BindView(R.id.problem_images_view_pager)
    ViewPager mProblemImagesViewPager;
    @BindView(R.id.problem_images_view_pager_indicator)
    CirclePageIndicator mProblemImagesViewPagerIndicator;
    @BindView(R.id.report_problem_type)
    EditText mReportProblemType;
    @BindView(R.id.report_problem_privacy_mode)
    Spinner mReportProblemPrivacyMode;
    @BindView(R.id.report_problem_description)
    EditText mReportProblemDescription;
    @BindView(R.id.report_problem_take_photo)
    FloatingActionButton mReportProblemTakePhoto;
    @BindView(R.id.report_problem_location_wrapper)
    TextInputLayout reportProblemLocationWrapper;
    @BindView(R.id.report_problem_description_wrapper)
    TextInputLayout reportProblemDescriptionWrapper;
    @BindView(R.id.report_problem_type_wrapper)
    TextInputLayout reportProblemTypeWrapper;

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
    String address;
    String[] photos;

    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_problem);

        ButterKnife.bind(this);

        DaggerNewProblemActivityComponent
            .builder()
            .appModule(new AppModule(this.getApplication()))
            .sharedPreferencesModule(new SharedPreferencesModule())
            .legacyApiModule(new LegacyApiModule())
            .build()
            .inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        initProblemImagesPager();
        initPrivacyModeSpinner();
    }

    private void initPrivacyModeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.report_privacy_mode, R.layout.item_report_type_spinner);
        adapter.setDropDownViewResource(R.layout.item_report_type_spinner_dropdown);
        mReportProblemPrivacyMode.setAdapter(adapter);
    }

    private void initProblemImagesPager() {
        mProblemImagesViewPager.setAdapter(ProblemImagesPagerAdapter.empty(this));
        mProblemImagesViewPager.setOffscreenPageLimit(3);
        mProblemImagesViewPagerIndicator.setViewPager(mProblemImagesViewPager);
        mProblemImagesViewPagerIndicator.setVisibility(View.GONE);
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
        if (validateProblemInputs()) {

            ProgressDialog progressDialog = createProgressDialog();
            progressDialog.show();

            photos = new String[problemImagesURIs.size()];

            Observable<String[]> photoObservable = Observable.from(problemImagesURIs)
                .map(uri -> Uri.fromFile(new File(uri.toString())))
                .map(uri -> {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                        return Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .toList()
                .map(photos -> {
                    String[] photoArray = new String[photos.size()];
                    photos.toArray(photoArray);
                    return photoArray;
                });

            Action1<ApiResponse<Integer>> onSuccess = apiResponse -> {
                if (apiResponse.getResult() != null) {
                    String newProblemId = apiResponse.getResult().toString();
                    myProblemsPreferences
                        .edit()
                        .putString(PROBLEM_PREFERENCE_KEY + newProblemId, newProblemId)
                        .apply();
                    EventBus.getDefault().post(new NewProblemAddedEvent());
                    progressDialog.dismiss();
                    Toast.makeText(this, R.string.problem_successfully_sent, Toast.LENGTH_SHORT).show();
                    finish();
                }
            };

            Action1<Throwable> onError = throwable -> {
                throwable.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.error_submitting_problem, Toast.LENGTH_SHORT).show();
            };

            photoObservable.flatMap(photos -> Observable.just(
                new GetNewProblemParams.Builder()
                    .setSessionId(ANONYMOUS_USER_SESSION_IS)
                    .setDescription(mReportProblemDescription.getText().toString())
                    .setType(reportType.getName())
                    .setAddress(address)
                    .setLatitude(locationCords.latitude)
                    .setLongitude(locationCords.longitude)
                    .setPhoto(photos)
                    .setEmail(null)
                    .setPhone(null)
                    .setMessageDescription(null)
                    .create())
            ).map(params -> new ApiRequest<>(ApiMethod.NEW_PROBLEM, params))
            .flatMap(request -> legacyApiService.postNewProblem(request))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                onSuccess,
                onError
            );
        }
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.sending_problem));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

    private boolean validateProblemInputs() {
        boolean addressIsValid = false;
        boolean descriptionIsValid = false;
        boolean problemTypeIsValid = false;

        if (address != null) {
            addressIsValid = true;
            reportProblemDescriptionWrapper.setError(null);
        } else {
            reportProblemLocationWrapper.setError(getText(R.string.error_problem_location_is_empty));
        }

        if (mReportProblemDescription.getText() != null && mReportProblemDescription.getText().length() > 0) {
            descriptionIsValid = true;
        } else {
            reportProblemDescriptionWrapper.setError(getText(R.string.error_problem_description_is_empty));
        }

        if (reportType != null) {
            problemTypeIsValid = true;
        } else {
            reportProblemTypeWrapper.setError(getText(R.string.error_problem_type_is_empty));
        }

        return addressIsValid && descriptionIsValid && problemTypeIsValid;
    }

    public void takePhoto() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.choose_photos_title);
        config.setCameraBtnImage(R.drawable.ic_photo_camera_white);

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
                    Toast.makeText(this, R.string.error_need_camera_and_storage_permission, Toast.LENGTH_SHORT).show();
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
                    LatLng latLng = place.getLatLng();

                    Geocoder geocoder = new Geocoder(this);

                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }
                    if (addresses != null && addresses.get(0).getLocality() != null) {
                        String city = addresses.get(0).getLocality();
                        if (city.equalsIgnoreCase(GlobalConsts.CITY_VILNIUS)) {
                            address = addresses.get(0).getAddressLine(0);
                            reportProblemLocationWrapper.setError(null);
                            mAddProblemLocation.setText(address);
                            locationCords = latLng;
                            if (snackbar != null && snackbar.isShown()) {
                                snackbar.dismiss();
                            }
                        } else {
                            showIncorrectPlaceSnackbar();
                        }
                    } else {
                        showIncorrectPlaceSnackbar();
                    }
                    break;
                case REQUEST_PROFILE:
                    profile = Profile.returnProfile(this);
                    break;
                case REQUEST_CHOOSE_REPORT_TYPE:
                    if (data.hasExtra(EXTRA_REPORT_TYPE)) {
                        reportType = data.getParcelableExtra(EXTRA_REPORT_TYPE);
                        reportProblemTypeWrapper.setError(null);
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

    private void showIncorrectPlaceSnackbar() {
        View view = this.getCurrentFocus();
        snackbar = Snackbar.make(view, R.string.error_location_incorrect, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.choose_again, v -> showPlacePicker(view));
        snackbar.show();
    }

    private void setPhotos(Uri[] photoUris) {
        if (photoUris.length > 1) {
            mProblemImagesViewPagerIndicator.setVisibility(View.VISIBLE);
        }
        mProblemImagesViewPager.setAdapter(new ProblemImagesPagerAdapter<Uri>(this, photoUris) {
            @Override
            public void loadImage(Uri imageURI, Context context, ImageView imageView) {
                Glide.with(context).load(new File(imageURI.getPath())).centerCrop().into(imageView);
            }
        });
    }

    @OnClick(R.id.report_problem_location)
    public void onProblemLocationClicked(View view) {
        showPlacePicker(view);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return false;
    }

    private void showPlacePicker(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(this);
            Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();

            ActivityCompat.startActivityForResult(this, intent, REQUEST_PLACE_PICKER, bundle);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.check_google_play_services, Toast.LENGTH_LONG).show();
        }
    }
}
