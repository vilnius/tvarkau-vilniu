package lt.vilnius.tvarkau.activity

import android.os.Bundle
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.fragments.ReportTypeListFragment

class ReportRegistrationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, ReportTypeListFragment.newInstance())
                .commit()
    }

    fun onTypeSelected(reportType: String) {
        //TODO next fragment
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.popBackStackImmediate()) {
            super.onBackPressed()
        }
    }
}
