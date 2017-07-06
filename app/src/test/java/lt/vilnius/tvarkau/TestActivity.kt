package lt.vilnius.tvarkau

import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.dagger.component.TestActivityComponent
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

    override fun buildComponent(application: TvarkauApplication): ActivityComponent {
        // create mock component for check injects
        return TestActivityComponent.init((application as TestTvarkauApplication).testComponent, this)
    }

    fun getTestComponent(): TestActivityComponent {
        return component as TestActivityComponent
    }
}