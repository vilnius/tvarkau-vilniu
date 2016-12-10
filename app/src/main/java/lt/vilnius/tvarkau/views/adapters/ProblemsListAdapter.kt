package lt.vilnius.tvarkau.views.adapters

import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.vinted.extensions.gone
import com.vinted.extensions.visible
import kotlinx.android.synthetic.main.problem_list_content.view.*
import lt.vilnius.tvarkau.ProblemDetailActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.extensions.applyReportStatusLabel
import lt.vilnius.tvarkau.utils.FormatUtils.formatLocalDateTime

class ProblemsListAdapter(
        private val activity: Activity,
        private val values: List<Problem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_ITEM = 1
    private val VIEW_PROGRESS = 0
    private var showLoader = false

    init {
        showLoader = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            DataViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.problem_list_content, parent, false))
        } else {
            ProgressViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.progress_indicator, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) {
            val item = values[position]
            val problemId = item.problemId

            item.applyReportStatusLabel(holder.itemView.problem_list_content_status)
            holder.itemView.problem_list_content_description.text = item.description
            holder.itemView.problem_list_content_title.text = item.getType()
            holder.itemView.problem_list_content_time.text = formatLocalDateTime(item.getEntryDate())

            if (item.photos != null) {
                holder.itemView.problem_list_content_thumb.visible()
                Glide.with(activity)
                        .load(item.photos!!.first())
                        .into(holder.itemView.problem_list_content_thumb)
            } else {
                holder.itemView.problem_list_content_thumb.gone()
            }

            holder.itemView.problem_list_content.setOnClickListener { view ->
                val intent = ProblemDetailActivity.getStartActivityIntent(activity, problemId)
                val bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0,
                        view.width, view.height).toBundle()

                ActivityCompat.startActivity(activity, intent, bundle)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < values.size) VIEW_ITEM else VIEW_PROGRESS
    }

    override fun getItemCount(): Int {
        return if (showLoader) values.size + 1 else values.size
    }

    fun hideLoader() {
        showLoader = false
    }

    inner class DataViewHolder(view: View) : RecyclerView.ViewHolder(view)
    inner class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
