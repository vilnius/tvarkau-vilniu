package lt.vilnius.tvarkau

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import lt.vilnius.tvarkau.fragments.ReportDetailsFragment

class ReportDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem_detail)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
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

        fun getStartActivityIntent(context: Context, problemId: Int): Intent {
            val intent = Intent(context, ReportDetailsActivity::class.java)
            intent.putExtra(ReportDetailsFragment.ARG_REPORT_ID, problemId)

            return intent
        }
    }
}
