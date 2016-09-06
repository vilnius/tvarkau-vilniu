package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    @BindView(R.id.share_contacts_switcher)
    Switch shareContactsSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ButterKnife.bind(this);
        shareContactsSwitcher.setOnCheckedChangeListener(this);
    }

    @OnClick(R.id.edit_personal_data)
    protected void onEditPersonalDataClick() {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivity(intent);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO edit sharedPreferences on Share contacts switch change
    }

    @OnClick(R.id.import_reports_from_previous_app)
    protected void onImportReportFromPreviousAppClick() {
        // TODO implement onImportReportFromPreviousAppClick
    }
}