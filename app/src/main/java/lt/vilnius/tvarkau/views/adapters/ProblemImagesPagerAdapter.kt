package lt.vilnius.tvarkau.views.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.problem_images_view_pager_item.view.*
import kotlinx.android.synthetic.main.problem_images_view_pager_map.view.*
import lt.vilnius.tvarkau.FullscreenImageActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.Problem


class ProblemImagesPagerAdapter(context: Context, private val photos: List<String>) : PagerAdapter() {

    private val layoutInflater: LayoutInflater
    private var problem: Problem?

    constructor(context: Context, problem: Problem) : this(context, problem.photos.orEmpty()) {
        this.problem = problem
    }

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        problem = null
    }

    override fun getCount(): Int {
        if (isEmpty()) {
            return 1
        }

        return photos.count() + if (problem != null) 1 else 0
    }

    fun isEmpty(): Boolean = photos.isEmpty() && problem == null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View

        if (isEmpty()) {
            itemView = instantiateEmptyPhoto(container)
        } else if (position == count - 1 && problem != null) {
            itemView = instantiateProblemMap(container)
        } else {
            itemView = instantiateProblemPhoto(position, container)
        }

        container.addView(itemView)

        return itemView
    }

    fun instantiateProblemMap(container: ViewGroup): View {
        layoutInflater.inflate(R.layout.problem_images_view_pager_map, container, false).run {
            if (problem_map_view != null) {
                problem_map_view.onCreate(null)
                problem_map_view.getMapAsync {
                    it.run {
                        moveCamera(CameraUpdateFactory.newLatLng(problem!!.latLng))
                        uiSettings.isMapToolbarEnabled = false

                        val marker = MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_selected))
                                .position(problem!!.latLng)

                        addMarker(marker)
                    }
                }
            }
            return this
        }
    }

    fun instantiateEmptyPhoto(container: ViewGroup): View =
            layoutInflater.inflate(R.layout.no_image, container, false)


    fun instantiateProblemPhoto(position: Int, container: ViewGroup): View {
        layoutInflater.inflate(R.layout.problem_images_view_pager_item, container, false).run {
            val photo = photos[position]

            Glide.with(context).load(photo).into(problem_image_view)

            problem_image_view.setOnClickListener { v ->
                val intent = Intent(context, FullscreenImageActivity::class.java)
                intent.putExtra(FullscreenImageActivity.EXTRA_PHOTOS, photos.toTypedArray<String>())
                intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_POSITION, position)
                context.startActivity(intent)
            }

            return this
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
