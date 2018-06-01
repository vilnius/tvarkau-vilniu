package lt.vilnius.tvarkau.viewmodel

import lt.vilnius.tvarkau.entity.Problem

sealed class ReportListViewModelState {
    abstract val reports: List<Problem>

    data class Loading(
            val refreshing: Boolean,
            override val reports: List<Problem>
    ) : ReportListViewModelState()

    data class Success(
            override val reports: List<Problem>
    ) : ReportListViewModelState()

    data class Error(
            val message: String,
            override val reports: List<Problem>
    ) : ReportListViewModelState()
}
