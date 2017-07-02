package lt.vilnius.tvarkau.navigation

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import lt.vilnius.tvarkau.R

class FragmentTransactionExecutor(private val fragmentManager: FragmentManager) {

    fun replaceWithClearTop(fragment: Fragment) {
        clearBackStackTop()
        replace(fragment, false)
    }

    @SuppressLint("CommitTransaction")
    fun replace(fragment: Fragment, addToBackStack: Boolean) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            setAllowOptimization(true)

            if (addToBackStack) {
                addToBackStack(null)
            }

            commitAllowingStateLoss()
        }
    }

    @SuppressLint("CommitTransaction")
    fun replaceWithVerticalAnimation(fragment: Fragment, addToBackStack: Boolean) {
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in_from_top, 0, 0, R.anim.slide_out_to_top)
            replace(R.id.container, fragment)


            if (addToBackStack) {
                addToBackStack(null)
            }

            commitAllowingStateLoss()
        }
    }

    private fun clearBackStackTop() {
        if (fragmentManager.backStackEntryCount > 0 && fragmentManager.getBackStackEntryAt(0) != null) {
            fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun isCurrentlyVisible(fragment: Fragment): Boolean {
        val prevFragment = fragmentManager.findFragmentById(R.id.container)
        return prevFragment != null && (fragment as Any).javaClass == (prevFragment as Any).javaClass
    }

}