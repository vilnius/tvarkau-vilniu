package lt.vilnius.tvarkau.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import lt.vilnius.tvarkau.ProfileEditActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.prefs.Preferences
import lt.vilnius.tvarkau.utils.DeviceUtils
import lt.vilnius.tvarkau.utils.SharedPrefsManager

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var prefsManager: SharedPrefsManager
    private lateinit var preferenceAbout: Preference
    private lateinit var preferenceImportReports: Preference
    private lateinit var preferenceUserPersonalData: Preference


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = Preferences.COMMON_PREFERENCES
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE

        setPreferencesFromResource(R.xml.preferences, rootKey)

        preferenceAbout = findPreference(KEY_PREFERENCE_ABOUT)
        preferenceImportReports = findPreference(KEY_PREFERENCE_IMPORT_REPORTS)
        preferenceUserPersonalData = findPreference(KEY_USER_PERSONAL_DATA)


        // TODO Do we really need to use our shared prefs manager ???
        // Can we override preferenceManager from PreferenceFragmentCompat
        prefsManager = SharedPrefsManager.getInstance(context)

        preferenceAbout.summary = getString(R.string.setting_about_summary, DeviceUtils.appVersion)

        preferenceImportReports.setOnPreferenceClickListener {
            showReportsImportDialog()
            true
        }

        preferenceUserPersonalData.setOnPreferenceChangeListener { _, newValue ->
            return@setOnPreferenceChangeListener when (newValue) {
                false -> {
                    startProfileEditActivity()
                    false
                }
                else -> true
            }
        }
        updateLastImportTime()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity.setTitle(R.string.title_settings)
    }

    private fun showReportsImportDialog() {
        val ft = childFragmentManager.beginTransaction()
        val reportImportDialog = ReportImportDialogFragment.newInstance(true)
        reportImportDialog.show(ft, REPORT_IMPORT_DIALOG)
    }

    private fun updateLastImportTime() {
        if (prefsManager.userLastReportImport != null) {
            preferenceImportReports.summary =
                    "${resources.getString(R.string.last_import)}  ${prefsManager.userLastReportImport}"
        }
    }

    private fun startProfileEditActivity() {
        Intent(context, ProfileEditActivity::class.java).run {
            startActivityForResult(this, REQUEST_EDIT_PROFILE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            // TODO notify changes
            Toast.makeText(context, R.string.personal_data_saved, Toast.LENGTH_SHORT).show()

        }
    }

    // TODO this should belong to ReportImportDialogFragment.SettingsVilniusSignInListener
    fun onVilniusSignIn() {
        Toast.makeText(context, R.string.report_import_done, Toast.LENGTH_SHORT).show()
        updateLastImportTime()
    }

    companion object {
        private const val REQUEST_EDIT_PROFILE = 1
        private const val REPORT_IMPORT_DIALOG = "report_import_dialog"

        private const val KEY_PREFERENCE_ABOUT = "preference_about"
        private const val KEY_PREFERENCE_IMPORT_REPORTS = "preference_import_reports"
        private const val KEY_USER_PERSONAL_DATA = "UserAnonymous"


        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}