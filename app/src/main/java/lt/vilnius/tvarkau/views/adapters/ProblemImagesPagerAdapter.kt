package lt.vilnius.tvarkau.views.adapters

import android.content.Context
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
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.ReportEntity


class ProblemImagesPagerAdapter(
    private val reportEntity: ReportEntity,
    private val onImageClicked: (position: Int, photos: List<String>) -> Unit = { _: Int, _: List<String> -> },
    private val onMapClicked: () -> Unit = {}
) : PagerAdapter() {

    override fun getCount(): Int = reportEntity.photos.count() + 1

    override fun isViewFromObject(view: View, any: Any): Boolean = view === any

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView: View

        itemView = if (position == count - 1) {
            instantiateProblemMap(
                layoutInflater.inflate(R.layout.problem_images_view_pager_map, container, false)
            )
        } else {
            instantiateProblemPhoto(
                position,
                layoutInflater.inflate(R.layout.problem_images_view_pager_item, container, false)
            )
        }

        container.addView(itemView)

        return itemView
    }

    private fun instantiateProblemMap(mapView: View): View {
        mapView.problem_map_view.onCreate(null)
        mapView.problem_map_view.getMapAsync { googleMap ->
            with(googleMap) {
                setOnMapClickListener { onMapClicked() }
                moveCamera(CameraUpdateFactory.newLatLng(reportEntity.latLng))
                uiSettings.isMapToolbarEnabled = false

                val marker = MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_selected))
                    .position(reportEntity.latLng)

                addMarker(marker)
            }
        }

        return mapView
    }


    private fun instantiateProblemPhoto(position: Int, photoView: View): View {
        val photo = reportEntity.photos[position]

        Glide.with(photoView.context).load(photo).into(photoView.problem_image_view)

        photoView.problem_image_view.setOnClickListener {
            onImageClicked(position, reportEntity.photos)
        }

        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) =
        container.removeView(any as View)
}

