package lt.vilnius.tvarkau.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_filter_report_type_row.view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.extensions.invisible
import lt.vilnius.tvarkau.extensions.visibleIf

/**
 * @author Martynas Jurkus
 */
class FilterReportTypesAdapter(
        val items: List<String>,
        var selected: String,
        val onReportTypeClicked: (String) -> Unit
) : RecyclerView.Adapter<FilterReportTypesAdapter.TypeViewHolder>() {

    override fun onBindViewHolder(vh: TypeViewHolder, position: Int) {
        val item = items[position]
        vh.itemView.filter_report_type.text = item
        vh.itemView.setOnClickListener {
            onReportTypeClicked(it.filter_report_type.text.toString())
        }

        vh.itemView.filter_report_type_selected.visibleIf(selected == item, { invisible() })
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