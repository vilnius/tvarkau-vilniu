package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_EDIT_PROFILE = 1;
    private SharedPrefsManager prefsManager;

    @BindView(R.id.share_contacts_switcher)
    Switch shareContactsSwitcher;

    @BindView(R.id.edit_personal_data)
    TextView editPersonalData;

    @BindView(R.id.settings_first_divider)
    View settingsFirstDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = SharedPrefsManager.getInstance(this);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        shareContactsSwitcher.setChecked(!prefsManager.isUserAnonymous());
        shareContactsSwitcher.setOnCheckedChangeListener(this);
        setUpEditPersonalData(prefsManager.isUserAnonymous());
    }

    @OnClick(R.id.edit_personal_data)
    protected void onEditPersonalDataClick() {
        startProfileEditActivity();
    }

    private void startProfileEditActivity() {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivityForResult(intent, REQUEST_EDIT_PROFILE);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (!prefsManager.isUserDetailsSaved()) {
                startProfileEditActivity();
            } else {
                setUpEditPersonalData(false);
            }
        } else {
            prefsManager.changeUserAnonymityStatus(true);
            setUpEditPersonalData(true);
        }
    }

    private void setUpEditPersonalData(boolean isUserAnonymous) {
        if (isUserAnonymous) {
            editPersonalData.setVisibility(View.GONE);
            settingsFirstDivider.setVisibility(View.GONE);

        } else {
            editPersonalData.setVisibility(View.VISIBLE);
            settingsFirstDivider.setVisibility(View.VISIBLE);
            editPersonalData.setClickable(true);
            editPersonalData.setTextColor(ContextCompat.getColor(this, R.color.black_87));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_PROFILE) {
            if (resultCode == RESULT_OK) {
                shareContactsSwitcher.setChecked(true);
                setUpEditPersonalData(false);
            } else {
                if (prefsManager.isUserAnonymous()) {
                    shareContactsSwitcher.setChecked(false);
                } else {
                    shareContactsSwitcher.setChecked(true);
                }
            }
        }
    }

    @OnClick(R.id.import_reports_from_previous_app)
    protected void onImportReportFromPreviousAppClick() {
        // TODO implement onImportReportFromPreviousAppClick
        Toast.makeText(this, "Sorry, not yet working, need to be implemented", Toast.LENGTH_LONG).show();
    }
}