package lt.vilnius.tvarkau

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_fullscreen.*
import lt.vilnius.tvarkau.views.adapters.FullscreenImagesPagerAdapter

class FullscreenImageActivity : BaseActivity() {

    private var initialImagePosition: Int = 0
    private lateinit var photos: Array<String>

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

        val extras = intent.extras
        if (extras != null) {
            initialImagePosition = extras.getInt(EXTRA_IMAGE_POSITION)
            photos = extras.getStringArray(EXTRA_PHOTOS)
        } else {
            photos = emptyArray()
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