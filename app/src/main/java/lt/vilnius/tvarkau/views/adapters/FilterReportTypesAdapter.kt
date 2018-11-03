package lt.vilnius.tvarkau.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_filter_report_type_row.view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.extensions.invisible
import lt.vilnius.tvarkau.extensions.visibleIf
import lt.vilnius.tvarkau.viewmodel.ReportFilterViewModel.ReportTypeViewEntity

class FilterReportTypesAdapter(
    val onReportTypeClicked: (ReportType) -> Unit
) : RecyclerView.Adapter<FilterReportTypesAdapter.TypeViewHolder>() {

    private val items = mutableListOf<ReportTypeViewEntity>()

    fun showReportTypes(reportTypes: List<ReportTypeViewEntity>) {
        items.clear()
        items.addAll(reportTypes)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(vh: TypeViewHolder, position: Int) {
        val item = items[position]
        vh.itemView.filter_report_type.text = item.reportType.title
        vh.itemView.setOnClickListener {
            onReportTypeClicked(item.reportType)
        }

        vh.itemView.filter_report_type_selected.visibleIf(item.selected) { invisible() }
    }

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        return TypeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.view_filter_report_type_row, parent, false)
        )
    }

    inner class TypeViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
