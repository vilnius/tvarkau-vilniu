package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    private SharedPrefsManager prefsManager;

    @BindView(R.id.share_contacts_switcher)
    Switch shareContactsSwitcher;

    @BindView(R.id.edit_personal_data)
    TextView editPersonalData;

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
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivity(intent);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            prefsManager.changeUserAnonymityStatus(false);
            setUpEditPersonalData(false);
        } else {
            prefsManager.changeUserAnonymityStatus(true);
            setUpEditPersonalData(true);
        }
    }

    private void setUpEditPersonalData(boolean isUserAnonymous){
        if (isUserAnonymous) {
            editPersonalData.setClickable(false);
            editPersonalData.setTextColor(ContextCompat.getColor(this, R.color.black_38));
        } else {
            editPersonalData.setClickable(true);
            editPersonalData.setTextColor(ContextCompat.getColor(this, R.color.black_87));
        }
    }

    @OnClick(R.id.import_reports_from_previous_app)
    protected void onImportReportFromPreviousAppClick() {
        // TODO implement onImportReportFromPreviousAppClick
    }
}