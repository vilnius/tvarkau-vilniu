package lt.vilnius.tvarkau.navigation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.interfaces.OnBackPressed

class FragmentTransactionExecutor(private val fragmentManager: FragmentManager) {

    fun replaceWithClearTop(fragment: Fragment) {
        clearBackStackTop()
        replace(fragment, false)
    }

    fun replace(fragment: Fragment, addToBackStack: Boolean) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .setAllowOptimization(true)
                .apply {
                    if (addToBackStack) addToBackStack(null)
                }
                .commitAllowingStateLoss()
    }

    fun replaceWithVerticalAnimation(fragment: Fragment, addToBackStack: Boolean) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_top, 0, 0, R.anim.slide_out_to_top)
                .replace(R.id.container, fragment)
                .apply {
                    if (addToBackStack) addToBackStack(null)
                }
                .commitAllowingStateLoss()
    }

    fun onBackPressed(): Boolean {
        val onBackPressed = fragmentManager.findFragmentById(R.id.container) as? OnBackPressed

        return onBackPressed?.onBackPressed() ?: false
    }


    private fun clearBackStackTop() {
        if (fragmentManager.backStackEntryCount > 0 && fragmentManager.getBackStackEntryAt(0) != null) {
            fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

}