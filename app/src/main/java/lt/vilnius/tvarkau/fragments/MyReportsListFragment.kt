package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_reports_list.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.MyReportListInteractor
import lt.vilnius.tvarkau.fragments.interactors.SharedPreferencesMyReportsInteractor
import lt.vilnius.tvarkau.fragments.presenters.MyReportListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.ProblemListPresenter
import lt.vilnius.tvarkau.fragments.views.ReportListView

/**
 * @author Martynas Jurkus
 */
class MyReportsListFragment : BaseReportListFragment(), ReportListView {

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

        my_problems_import.setOnClickListener {
            val listener = activity as BaseReportListFragment.OnImportReportClickListener
            listener.onImportReportClick()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseActivity?.setTitle(R.string.home_my_problems)
    }

    override fun showEmptyState() {
        my_problems_empty_view.visible()
    }

    override fun hideEmptyState() {
        my_problems_empty_view.gone()
    }

    companion object {
        fun newInstance() = MyReportsListFragment()
    }
}