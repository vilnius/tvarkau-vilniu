package lt.vilnius.tvarkau

import android.os.Bundle
import android.os.Parcelable
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.fragments.*
import lt.vilnius.tvarkau.utils.GlobalConsts
import org.parceler.Parcels

class ProblemsMapActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.problems_map_activity)

        val data = intent.extras
        if (data != null && savedInstanceState == null) {
            val fragment: BaseMapFragment
            val fragmentTag = data.getString(GlobalConsts.KEY_MAP_FRAGMENT)

            when (fragmentTag) {
                GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT -> {
                    val problem = Parcels.unwrap<Problem>(data.getParcelable<Parcelable>(ProblemDetailFragment.KEY_PROBLEM))
                    fragment = SingleProblemMapFragment.getInstance(problem)
                }
                GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT -> fragment = MultipleProblemsMapFragment.newInstance()
                else -> return
            }

            supportFragmentManager.beginTransaction()
                    .replace(R.id.problems_map_frame, fragment)
                    .commit()
        }
    }

    fun openFilters() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_top, 0, 0, R.anim.slide_out_to_top)
                .replace(R.id.problems_map_frame, ReportFilterFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.problems_map_frame) as? BaseFragment
        if (fragment?.onBackPressed() ?: false) {
            return
        }

        super.onBackPressed()
    }
}
