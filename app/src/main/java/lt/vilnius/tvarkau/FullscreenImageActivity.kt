package lt.vilnius.tvarkau

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_fullscreen.*
import lt.vilnius.tvarkau.views.adapters.FullscreenImagesPagerAdapter

class FullscreenImageActivity : BaseActivity() {

    private val initialImagePosition: Int
        get() = intent.getIntExtra(EXTRA_IMAGE_POSITION, 0)

    private val photos: Array<String>
        get() = intent.getStringArrayExtra(EXTRA_PHOTOS) ?: emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setContentView(R.layout.activity_fullscreen)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
            hide()
        }

        initializePager()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun initializePager() {
        problem_images_view_pager.adapter = FullscreenImagesPagerAdapter(this, photos) {
            supportActionBar?.apply {
                if (isShowing) {
                    hide()
                } else {
                    show()
                }
            }
        }
        problem_images_view_pager.offscreenPageLimit = 3
        problem_images_view_pager.currentItem = initialImagePosition
    }

    companion object {
        val EXTRA_IMAGE_POSITION = "FullscreenImageActivity.position"
        val EXTRA_PHOTOS = "FullscreenImageActivity.photos"
    }
}