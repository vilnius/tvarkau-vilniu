package lt.vilnius.tvarkau.fragments.presenters

/**
 * @author Martynas Jurkus
 */
interface ProblemListPresenter {
    fun onAttach()
    fun onDetach()
    fun getReportsForPage(page: Int)
}