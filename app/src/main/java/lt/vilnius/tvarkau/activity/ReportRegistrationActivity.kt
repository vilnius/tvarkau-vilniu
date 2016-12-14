package lt.vilnius.tvarkau.activity

import android.os.Bundle
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.fragments.NewReportFragment
import lt.vilnius.tvarkau.fragments.ReportTypeListFragment

class ReportRegistrationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(android.R.id.content, ReportTypeListFragment.newInstance())
                    .commit()
        }
    }

    fun onTypeSelected(reportType: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, NewReportFragment.newInstance(reportType))
                .addToBackStack(null)
                .commit()
    }

    fun onReportSubmitted() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.popBackStackImmediate()) {
            super.onBackPressed()
        }
    }
}
