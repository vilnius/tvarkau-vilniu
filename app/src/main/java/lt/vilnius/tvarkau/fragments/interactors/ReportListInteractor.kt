package lt.vilnius.tvarkau.fragments.interactors

import lt.vilnius.tvarkau.entity.Problem
import rx.Single

/**
 * @author Martynas Jurkus
 */
interface ReportListInteractor {

    fun getProblems(page: Int): Single<List<Problem>>
}