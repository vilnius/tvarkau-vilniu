package lt.vilnius.tvarkau

import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController


class TestActivity : BaseActivity() {

    companion object {
        fun newInstance(): ActivityController<TestActivity> {
            return Robolectric.buildActivity(TestActivity::class.java).create(null)
        }
    }
}
