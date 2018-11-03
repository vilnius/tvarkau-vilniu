package lt.vilnius.tvarkau

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.fragments.ReportDetailsFragment

class ReportDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_details)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            val reportId = intent.getIntExtra(ReportDetailsFragment.ARG_REPORT_ID, 0)
            val fragment = ReportDetailsFragment.getInstance(reportId)
            supportFragmentManager.beginTransaction()
                .add(R.id.problem_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        fun getStartActivityIntent(context: Context, reportId: Int): Intent {
            val intent = Intent(context, ReportDetailsActivity::class.java)
            intent.putExtra(ReportDetailsFragment.ARG_REPORT_ID, reportId)

            return intent
        }
    }
}
