package lt.vilnius.tvarkau.extensions

import android.widget.TextView
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.Problem

/**
 * @author Martynas Jurkus
 */
fun Problem.applyReportStatusLabel(reportLabelTextView: TextView) {
    reportLabelTextView.text = status
    if (status.equals(Problem.STATUS_DONE, ignoreCase = true) || status.equals(Problem.STATUS_RESOLVED, ignoreCase = true)) {
        reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_done)
    } else if (status.equals(Problem.STATUS_POSTPONED, ignoreCase = true)) {
        reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_postponed)
    } else if (status.equals(Problem.STATUS_REGISTERED, ignoreCase = true)) {
        reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_registered)
    } else if (status.equals(Problem.STATUS_TRANSFERRED, ignoreCase = true)) {
        reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_transferred)
    }
}