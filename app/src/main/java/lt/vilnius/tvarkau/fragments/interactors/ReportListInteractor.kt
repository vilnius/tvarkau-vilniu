package lt.vilnius.tvarkau.fragments.interactors

import io.reactivex.Single
import lt.vilnius.tvarkau.entity.Problem

/**
 * @author Martynas Jurkus
 */
interface ReportListInteractor {

    fun getProblems(page: Int): Single<List<Problem>>
}