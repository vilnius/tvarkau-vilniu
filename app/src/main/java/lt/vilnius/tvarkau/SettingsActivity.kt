package lt.vilnius.tvarkau

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.getColor
import android.widget.CompoundButton
import android.widget.Toast
import com.vinted.extensions.gone
import com.vinted.extensions.visible
import kotlinx.android.synthetic.main.settings_activity.*
import lt.vilnius.tvarkau.ProblemsListActivity.MY_PROBLEMS
import lt.vilnius.tvarkau.fragments.ReportImportDialogFragment
import lt.vilnius.tvarkau.utils.SharedPrefsManager

class SettingsActivity : BaseActivity(),
        CompoundButton.OnCheckedChangeListener,
        ReportImportDialogFragment.SettingsVilniusSignInListener {

    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        prefsManager = SharedPrefsManager.getInstance(this)
        share_contacts_switcher.isChecked = !prefsManager.isUserAnonymous
        share_contacts_switcher.setOnCheckedChangeListener(this)
        setUpedit_personal_data(prefsManager.isUserAnonymous)
        if (prefsManager.userLastReportImport != null) {
            last_import.visible()
            last_import.text = "${resources.getString(R.string.last_import)}  ${prefsManager.userLastReportImport}"
            login_to_vilnius_account.gone()
        } else {
            last_import.gone()
            login_to_vilnius_account.visible()
        }

        edit_personal_data.setOnClickListener { startProfileEditActivity() }
        import_reports_from_previous_app.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            val reportImportDialog = ReportImportDialogFragment.newInstance(true)
            reportImportDialog.show(ft, REPORT_IMPORT_DIALOG)
        }
    }

    private fun startProfileEditActivity() {
        Intent(this, ProfileEditActivity::class.java).run {
            startActivityForResult(this, REQUEST_EDIT_PROFILE)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            if (!prefsManager.isUserDetailsSaved) {
                startProfileEditActivity()
            } else {
                setUpedit_personal_data(false)
                prefsManager.changeUserAnonymityStatus(false)
            }
        } else {
            prefsManager.changeUserAnonymityStatus(true)
            setUpedit_personal_data(true)
            analytics.trackPersonalDataSharingEnabled(enabled = false)
        }
    }

    private fun setUpedit_personal_data(isUserAnonymous: Boolean) {
        if (isUserAnonymous) {
            edit_personal_data.gone()
            settings_first_divider.gone()
        } else {
            edit_personal_data.visible()
            settings_first_divider.visible()
            edit_personal_data.isClickable = true
            edit_personal_data.setTextColor(getColor(this, R.color.black_87))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_PROFILE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    share_contacts_switcher.isChecked = true
                    setUpedit_personal_data(false)
                    Toast.makeText(this, R.string.personal_data_saved, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    share_contacts_switcher.isChecked = !prefsManager.isUserAnonymous
                }
            }
        }
    }

    override fun onVilniusSignIn() {
        Snackbar.make(settings_layout, R.string.report_import_done, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.check_imported_reports) {
                    ProblemsListActivity.getStartActivityIntent(this, MY_PROBLEMS).run {
                        startActivity(this)
                    }
                }
                .setActionTextColor(getColor(this, R.color.snackbar_action_text))
                .show()

        last_import.visible()
        last_import.text = "${resources.getString(R.string.last_import)} ${prefsManager.userLastReportImport}"
        login_to_vilnius_account.gone()
    }

    companion object {
        private val REQUEST_EDIT_PROFILE = 1
        private val REPORT_IMPORT_DIALOG = "report_import_dialog"
    }
}