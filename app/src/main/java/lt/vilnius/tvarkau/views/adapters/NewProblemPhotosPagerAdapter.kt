package lt.vilnius.tvarkau.views.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.problem_images_view_pager_item.view.*
import lt.vilnius.tvarkau.R
import java.io.File


class NewProblemPhotosPagerAdapter(private val photos: List<File>,
                                   private val listener: OnPhotoClickedListener) : PagerAdapter() {

    override fun getCount(): Int = photos.count()

    override fun isViewFromObject(view: View, any: Any): Boolean = view === any

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView = instantiateProblemPhoto(
                position,
                layoutInflater.inflate(R.layout.problem_images_view_pager_item, container, false)
        )

        container.addView(itemView)

        return itemView
    }

    fun instantiateProblemPhoto(position: Int, view: View): View {
        Glide.with(view.context).load(photos[position]).into(view.problem_image_view)

        view.problem_image_view.setOnClickListener { listener.onPhotoClicked(position, photos.map(File::toString)) }

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) =
            container.removeView(any as View)


    interface OnPhotoClickedListener {
        fun onPhotoClicked(position: Int, photos: List<String>)
    }

}
