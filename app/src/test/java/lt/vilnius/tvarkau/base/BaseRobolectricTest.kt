package lt.vilnius.tvarkau.base

import android.support.v4.app.Fragment
import lt.vilnius.tvarkau.TestActivity
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ActivityController


/**
 * @author Martynas Jurkus
 */
@RunWith(RobolectricTestRunner::class)
abstract class BaseRobolectricTest {

    protected lateinit var activity: TestActivity
    protected var activityController: ActivityController<TestActivity>? = null

    @Before
    open fun setUp() {
        activityController = TestActivity.newInstance().start().resume()

        activityController?.let { activity = it.get() }
    }

    @After
    fun tearDown() {
        activityController?.destroy()
    }

    protected fun setFragment(fragment: Fragment) {
        activity.supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        activity.fragmentManager.executePendingTransactions()
    }
}