package lt.vilnius.tvarkau

import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.fragments.BaseFragment
import lt.vilnius.tvarkau.fragments.BaseMapFragment
import lt.vilnius.tvarkau.fragments.ProblemDetailFragment
import lt.vilnius.tvarkau.fragments.SingleProblemMapFragment
import lt.vilnius.tvarkau.utils.GlobalConsts
import org.parceler.Parcels

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
                    val problem = Parcels.unwrap<Problem>(data.getParcelable<Parcelable>(ProblemDetailFragment.KEY_PROBLEM))
                    fragment = SingleProblemMapFragment.getInstance(problem)
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
        if (fragment?.onBackPressed() ?: false) {
            return
        }

        super.onBackPressed()
    }
}
