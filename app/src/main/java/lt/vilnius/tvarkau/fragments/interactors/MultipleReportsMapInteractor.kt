package lt.vilnius.tvarkau.fragments.interactors

import lt.vilnius.tvarkau.entity.Problem
import rx.Single

/**
 * @author Martynas Jurkus
 */
interface MultipleReportsMapInteractor {

    fun getReports(): Single<List<Problem>>
}