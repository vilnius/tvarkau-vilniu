package lt.vilnius.tvarkau.fragments

import android.support.v7.util.DiffUtil
import lt.vilnius.tvarkau.entity.ReportEntity


class ReportDiffUtilCallback : DiffUtil.ItemCallback<ReportEntity>() {
    override fun areItemsTheSame(oldItem: ReportEntity?, newItem: ReportEntity?): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ReportEntity?, newItem: ReportEntity?): Boolean {
        return oldItem == newItem
    }
}
