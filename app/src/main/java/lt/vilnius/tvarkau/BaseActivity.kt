package lt.vilnius.tvarkau

import android.arch.lifecycle.ViewModelProvider
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import lt.vilnius.tvarkau.analytics.Analytics
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var analytics: Analytics
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onDestroy() {
        analytics.flush()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
