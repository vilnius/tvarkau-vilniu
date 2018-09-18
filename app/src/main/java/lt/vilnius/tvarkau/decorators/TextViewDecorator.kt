package lt.vilnius.tvarkau.decorators

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.utils.TextUtils

class TextViewDecorator(
    private val textView: TextView
) {

    fun decorateProblemIdSpans(text: String) {
        val stringBuilder = SpannableStringBuilder(text)
        val idOccurrences = TextUtils.findReportREferenceNumberOccurrences(text)

        var start = 0
        var end: Int
        for (occurrence in idOccurrences) {
            start = text.indexOf(occurrence, start)
            end = start + occurrence.length
            stringBuilder.setSpan(
                ReportReferenceNumberSpan(textView.context, occurrence),
                start,
                end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            start = end //update start position to look for next occurrence
        }

        textView.setText(stringBuilder, TextView.BufferType.SPANNABLE)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }


    class ReportReferenceNumberSpan(
        private val context: Context,
        private val reportReferenceNumber: String
    ) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(context, R.color.problem_address)
        }

        override fun onClick(view: View) {
            //FIXME lookup report by reference number and start details activity
//            val intent = ReportDetailsActivity.getStartActivityIntent(
//                context,
//                reportId
//            )

//            ActivityCompat.startActivity(context, intent, null)
        }
    }
}
