package lt.vilnius.tvarkau

import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.dagger.component.TestApplicationComponent
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController


/**
 * @author Martynas Jurkus
 */
class TestActivity : BaseActivity() {

    companion object {
        fun newInstance(): ActivityController<TestActivity> {
            return Robolectric.buildActivity(TestActivity::class.java).create(null)
        }
    }

    override fun buildComponent(application: TvarkauApplication): ApplicationComponent {
        // create mock component for check injects
        return (application as TestTvarkauApplication).testComponent
    }

    fun getTestComponent(): TestApplicationComponent {
        return component as TestApplicationComponent
    }
}