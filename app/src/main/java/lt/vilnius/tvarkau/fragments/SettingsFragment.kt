package lt.vilnius.tvarkau.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.dagger.module.IoScheduler
import lt.vilnius.tvarkau.dagger.module.UiScheduler
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent
import lt.vilnius.tvarkau.navigation.NavigationManager
import lt.vilnius.tvarkau.prefs.Preferences
import lt.vilnius.tvarkau.rx.RxBus
import lt.vilnius.tvarkau.utils.DeviceUtils
import lt.vilnius.tvarkau.utils.SharedPrefsManager
import rx.Scheduler
import rx.Subscription
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @field:[Inject IoScheduler]
    lateinit var ioScheduler: Scheduler
    @field:[Inject UiScheduler]
    lateinit var uiScheduler: Scheduler

    @Inject
    lateinit var navigationManager: NavigationManager

    private lateinit var prefsManager: SharedPrefsManager
    private lateinit var preferenceAbout: Preference
    private lateinit var preferenceImportReports: Preference
    private lateinit var preferenceUserPersonalData: Preference

    private var subscription: Subscription? = null


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        ActivityComponent.init((activity!!.application as TvarkauApplication).component, activity!! as AppCompatActivity).inject(this)

        // Do we really need to use our shared prefs manager ???
        // Can we override preferenceManager from PreferenceFragmentCompat
        prefsManager = SharedPrefsManager.getInstance(context)

        createPreferences()
        updateLastImportTime()
    }

    private fun createPreferences() {
        preferenceManager.sharedPreferencesName = Preferences.COMMON_PREFERENCES
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE

        setPreferencesFromResource(R.xml.preferences, null)

        preferenceAbout = findPreference(KEY_PREFERENCE_ABOUT)
        preferenceImportReports = findPreference(KEY_PREFERENCE_IMPORT_REPORTS)
        preferenceUserPersonalData = findPreference(KEY_USER_PERSONAL_DATA)

        preferenceAbout.summary = getString(R.string.setting_about_summary, DeviceUtils.appVersion)

        preferenceImportReports.setOnPreferenceClickListener {
            navigationManager.showReportsImportDialog()
            true
        }

        preferenceUserPersonalData.setOnPreferenceChangeListener { _, newValue ->
            return@setOnPreferenceChangeListener when (newValue) {
                false -> {
                    navigationManager.navigateToProfileEditActivity()
                    false
                }
                else -> true
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity!!.setTitle(R.string.title_settings)

        RxBus.observable
                .filter { it is NewProblemAddedEvent }
                .limit(1)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    updateLastImportTime()
                }).apply { subscription = this }
    }

    private fun updateLastImportTime() {
        if (prefsManager.userLastReportImport != null) {
            preferenceImportReports.summary =
                    "${resources.getString(R.string.last_import)}  ${prefsManager.userLastReportImport}"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ActivityConstants.REQUEST_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            createPreferences()
            Toast.makeText(context, R.string.personal_data_saved, Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroyView() {
        subscription?.unsubscribe()

        super.onDestroyView()
    }

    companion object {

        private const val KEY_PREFERENCE_ABOUT = "preference_about"
        private const val KEY_PREFERENCE_IMPORT_REPORTS = "preference_import_reports"
        private const val KEY_USER_PERSONAL_DATA = "UserAnonymous"


        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}