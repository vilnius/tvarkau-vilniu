package lt.vilnius.tvarkau.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_report_type.view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.ReportType

class ReportTypesListAdapter(
    private val reportTypes: List<ReportType>,
    private val onSelectedListener: (ReportType) -> Unit = {}
) : RecyclerView.Adapter<ReportTypesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportTypesListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_type, parent, false)

        return ReportTypesListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportTypesListAdapter.ViewHolder, position: Int) {
        val reportType = reportTypes[position]

        holder.itemView.item_report_type_name.text = reportType.title
        holder.itemView.setOnClickListener {
            onSelectedListener(reportType)
        }

        //TODO move this to backend
//        if (holder.reportTypeName!!.text == "Transporto priemonių stovėjimo tvarkos pažeidimai") {
//            holder.reportTypeName!!.setText(R.string.transport_vehicle_report_type_contacts_needed)
//        }
    }

    override fun getItemCount() = reportTypes.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
