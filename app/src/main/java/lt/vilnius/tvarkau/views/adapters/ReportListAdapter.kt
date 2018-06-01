package lt.vilnius.tvarkau.views.adapters

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.problem_list_content.view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.extensions.applyReportStatusLabel
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible

class ReportListAdapter(
    diffCallback: DiffUtil.ItemCallback<ReportEntity>,
    private val onReportClicked: (view: View, reportId: Int) -> Unit
) : PagedListAdapter<ReportEntity, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DataViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.problem_list_content, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        item.applyReportStatusLabel(holder.itemView.problem_list_content_status)
        holder.itemView.problem_list_content_title.text = item.reportType.title
        holder.itemView.problem_list_content_description.text = item.description
        holder.itemView.problem_list_content_time.text = item.registeredAt


        if (item.photos.isEmpty()) {
            holder.itemView.problem_list_content_thumb.gone()
        } else {
            holder.itemView.problem_list_content_thumb.visible()
            Glide.with(holder.itemView.context)
                .load(item.photos.first())
                .into(holder.itemView.problem_list_content_thumb)
        }

        holder.itemView.problem_list_content.setOnClickListener { onReportClicked(it, item.id) }
    }

    inner class DataViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
