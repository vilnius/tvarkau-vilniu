package lt.vilnius.tvarkau

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.problems_list_activity.*
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity
import lt.vilnius.tvarkau.fragments.ProblemsListFragment
import lt.vilnius.tvarkau.fragments.ReportImportDialogFragment
import lt.vilnius.tvarkau.utils.GlobalConsts
import lt.vilnius.tvarkau.views.adapters.ProblemsListViewPagerAdapter

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

        if (intent.extras != null) {
            initialPosition = intent.extras.getInt(EXTRA_INITIAL_POSITION, ALL_PROBLEMS)
        }

        setTabs()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fab_report_problem.setOnClickListener {
            val intent = Intent(this, ReportRegistrationActivity::class.java)
            val bundle = ActivityOptionsCompat.makeScaleUpAnimation(it, 0, 0,
                    it.width, it.height).toBundle()

            ActivityCompat.startActivity(this, intent, bundle)
        }
    }

    fun setTabs() {
        if (problems_list_view_pager!!.adapter == null) {
            problems_list_view_pager!!.adapter = ProblemsListViewPagerAdapter(this, supportFragmentManager)
            problems_list_tab_layout!!.setupWithViewPager(problems_list_view_pager)
            problems_list_view_pager!!.currentItem = initialPosition
            problems_list_view_pager!!.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

                override fun onPageSelected(position: Int) {
                    supportInvalidateOptionsMenu()
                }
            })
        }
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
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)

        if (problems_list_view_pager != null && problems_list_view_pager!!.currentItem == MY_PROBLEMS) {
            menu.findItem(R.id.action_filter).isVisible = false
        }
        return true
    }

    override fun onImportReportClick() {
        val ft = supportFragmentManager.beginTransaction()
        val reportImportDialog = ReportImportDialogFragment.newInstance(false)
        reportImportDialog.show(ft, REPORT_IMPORT_DIALOG)
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
