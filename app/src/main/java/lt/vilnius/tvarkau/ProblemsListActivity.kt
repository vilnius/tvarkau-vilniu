package lt.vilnius.tvarkau

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.MenuItem
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.problems_list_activity.*
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.fragments.BaseFragment
import lt.vilnius.tvarkau.fragments.ProblemsListFragment
import lt.vilnius.tvarkau.fragments.ReportFilterFragment
import lt.vilnius.tvarkau.fragments.ReportFilterFragment.Companion.TARGET_LIST
import lt.vilnius.tvarkau.fragments.ReportImportDialogFragment
import lt.vilnius.tvarkau.utils.GlobalConsts

/**
 * An activity representing a list of Problems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ProblemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ProblemsListActivity : BaseActivity(), ProblemsListFragment.OnImportReportClickListener {

    @IntDef(ALL_PROBLEMS.toLong(), MY_PROBLEMS.toLong())
    internal annotation class ProblemsTabsInitialPosition

    private var initialPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.problems_list_activity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.extras != null) {
            initialPosition = intent.extras.getInt(EXTRA_INITIAL_POSITION, ALL_PROBLEMS)
        }


        initFragment()

        fab_report_problem.setOnClickListener {
            val intent = Intent(this, ReportRegistrationActivity::class.java)
            val bundle = ActivityOptionsCompat.makeScaleUpAnimation(it, 0, 0,
                    it.width, it.height).toBundle()

            ActivityCompat.startActivity(this, intent, bundle)
        }
    }

    private fun initFragment() {
        val fragment = when (initialPosition) {
            MY_PROBLEMS -> ProblemsListFragment.myReportsListInstance()
            else -> ProblemsListFragment.allReportsListInstance()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_map -> {
                val intent = Intent(this, ProblemsMapActivity::class.java)
                intent.putExtra(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT)
                startActivity(intent)

                return true
            }
            R.id.action_filter -> {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_from_top, 0, 0, R.anim.slide_out_to_top)
                        .replace(R.id.fragment_container, ReportFilterFragment.newInstance(TARGET_LIST))
                        .addToBackStack(null)
                        .commit()

                fab_report_problem.hide()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onImportReportClick() {
        val ft = supportFragmentManager.beginTransaction()
        val reportImportDialog = ReportImportDialogFragment.newInstance(false)
        reportImportDialog.show(ft, REPORT_IMPORT_DIALOG)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? BaseFragment
        if (fragment?.onBackPressed() ?: false) {
            return
        }

        fab_report_problem.show()
        super.onBackPressed()
    }

    companion object {

        const val ALL_PROBLEMS = 0
        const val MY_PROBLEMS = 1
        private val REPORT_IMPORT_DIALOG = "report_import_dialog"

        private val EXTRA_INITIAL_POSITION = "list.initial_position"

        fun getStartActivityIntent(context: Context, @ProblemsTabsInitialPosition initialPosition: Int): Intent {
            val intent = Intent(context, ProblemsListActivity::class.java)

            intent.putExtra(EXTRA_INITIAL_POSITION, initialPosition)

            return intent
        }
    }
}
