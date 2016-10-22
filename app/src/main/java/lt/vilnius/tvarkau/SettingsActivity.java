package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.fragments.ReportImportDialogFragment;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

import static lt.vilnius.tvarkau.ProblemsListActivity.MY_PROBLEMS;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,
    ReportImportDialogFragment.SettingsVilniusSignInListener {

    private static final int REQUEST_EDIT_PROFILE = 1;
    private static final String REPORT_IMPORT_DIALOG = "report_import_dialog";
    private SharedPrefsManager prefsManager;

    @BindView(R.id.share_contacts_switcher)
    SwitchCompat shareContactsSwitcher;

    @BindView(R.id.edit_personal_data)
    TextView editPersonalData;

    @BindView(R.id.settings_first_divider)
    View settingsFirstDivider;

    @BindView(R.id.settings_layout)
    View settingsLayout;

    @BindView(R.id.login_to_vilnius_account)
    TextView loginToVilniusAccount;

    @BindView(R.id.last_import)
    TextView lastImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = SharedPrefsManager.getInstance(this);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        shareContactsSwitcher.setChecked(!prefsManager.isUserAnonymous());
        shareContactsSwitcher.setOnCheckedChangeListener(this);
        setUpEditPersonalData(prefsManager.isUserAnonymous());
        if (prefsManager.getUserLastReportImport() != null) {
            lastImport.setVisibility(View.VISIBLE);
            lastImport.setText(getResources().getString(R.string.last_import) + " " + prefsManager.getUserLastReportImport());
            loginToVilniusAccount.setVisibility(View.GONE);
        } else {
            lastImport.setVisibility(View.GONE);
            loginToVilniusAccount.setVisibility(View.VISIBLE);
        }
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
                Toast.makeText(this, R.string.personal_data_saved, Toast.LENGTH_SHORT).show();
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ReportImportDialogFragment reportImportDialog = ReportImportDialogFragment.newInstance(true);
        reportImportDialog.show(ft, REPORT_IMPORT_DIALOG);
    }

    @Override public void onVilniusSignIn() {
       Snackbar.make(settingsLayout, R.string.report_import_done, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.check_imported_reports, v -> {
                Intent intent = ProblemsListActivity.getStartActivityIntent(this, MY_PROBLEMS);
                startActivity(intent);
            })
            .setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_text))
            .show();
        lastImport.setVisibility(View.VISIBLE);
        lastImport.setText(getResources().getString(R.string.last_import) + " " + prefsManager.getUserLastReportImport());
        loginToVilniusAccount.setVisibility(View.GONE);
    }
}