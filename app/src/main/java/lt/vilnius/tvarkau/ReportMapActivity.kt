package lt.vilnius.tvarkau

import android.os.Bundle
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.fragments.BaseFragment
import lt.vilnius.tvarkau.fragments.BaseMapFragment
import lt.vilnius.tvarkau.fragments.ReportDetailsFragment
import lt.vilnius.tvarkau.fragments.SingleProblemMapFragment
import lt.vilnius.tvarkau.utils.GlobalConsts

class ReportMapActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.problems_map_activity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val data = intent.extras
        if (data != null && savedInstanceState == null) {
            val fragment: BaseMapFragment
            val fragmentTag = data.getString(GlobalConsts.KEY_MAP_FRAGMENT)

            when (fragmentTag) {
                GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT -> {
                    val reportEntity = data.getParcelable<ReportEntity>(ReportDetailsFragment.KEY_PROBLEM)
                    fragment = SingleProblemMapFragment.getInstance(reportEntity)
                }
                else -> throw IllegalArgumentException("Please pass problem")
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }


    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment
        if (fragment?.onBackPressed() == true) {
            return
        }

        super.onBackPressed()
    }
}
