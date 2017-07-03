package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fab_new_report.*
import kotlinx.android.synthetic.main.fragment_my_reports_list.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.dagger.component.MainActivityComponent
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.MyReportListInteractor
import lt.vilnius.tvarkau.fragments.interactors.SharedPreferencesMyReportsInteractor
import lt.vilnius.tvarkau.fragments.presenters.MyReportListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.ProblemListPresenter
import lt.vilnius.tvarkau.fragments.views.ReportListView
import lt.vilnius.tvarkau.navigation.NavigationManager
import javax.inject.Inject

/**
 * @author Martynas Jurkus
 */
class MyReportsListFragment : BaseReportListFragment(), ReportListView, BaseReportListFragment.OnImportReportClickListener {

    @Inject
    lateinit var navigationManager: NavigationManager

    override val presenter: ProblemListPresenter by lazy {
        MyReportListPresenterImpl(
                MyReportListInteractor(
                        legacyApiService,
                        SharedPreferencesMyReportsInteractor(myProblemsPreferences),
                        ioScheduler
                ),
                uiScheduler,
                this,
                connectivityProvider
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_reports_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        my_problems_import.setOnClickListener { onImportReportClick() }
    }

    override fun onInject(component: ApplicationComponent) {
        MainActivityComponent.init(component, activity as AppCompatActivity).inject(this)
    }

    override fun onImportReportClick() {
        val ft = activity.supportFragmentManager.beginTransaction()
        val reportImportDialog = ReportImportDialogFragment.newInstance(false)
        reportImportDialog.show(ft, REPORT_IMPORT_DIALOG)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseActivity?.setTitle(R.string.title_my_problem_list)
        fab_report.setOnClickListener { navigationManager.navigateToNewReport() }
    }

    override fun showEmptyState() {
        my_problems_empty_view.visible()
    }

    override fun hideEmptyState() {
        my_problems_empty_view.gone()
    }

    companion object {
        private const val REPORT_IMPORT_DIALOG = "report_import_dialog"

        fun newInstance() = MyReportsListFragment()
    }
}