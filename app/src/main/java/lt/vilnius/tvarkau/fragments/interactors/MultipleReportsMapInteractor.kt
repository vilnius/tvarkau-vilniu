package lt.vilnius.tvarkau.fragments.interactors

import io.reactivex.Single
import lt.vilnius.tvarkau.entity.Problem

/**
 * @author Martynas Jurkus
 */
interface MultipleReportsMapInteractor {

    fun getReports(): Single<List<Problem>>
}