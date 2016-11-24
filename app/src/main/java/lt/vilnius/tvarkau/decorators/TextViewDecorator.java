package lt.vilnius.tvarkau.decorators;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.utils.TextUtils;

/**
 * @author Martynas Jurkus
 */

public class TextViewDecorator {

    private TextView textView;

    public TextViewDecorator(TextView textView) {
        this.textView = textView;
    }

    public void decorateProblemIdSpans(String text) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        List<String> idOccurrences = TextUtils.findProblemIdOccurrences(text);

        int start = 0;
        int end;
        for (String occurrence : idOccurrences) {
            start = text.indexOf(occurrence, start);
            end = start + occurrence.length();
            stringBuilder.setSpan(new ProblemIssueIdSpan(textView.getContext(), occurrence),
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            start = end; //update start position to look for next occurrence
        }

        textView.setText(stringBuilder, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    static class ProblemIssueIdSpan extends ClickableSpan {

        private Context context;
        private String issueId;

        private ProblemIssueIdSpan(Context context, String issueId) {
            this.context = context;
            this.issueId = issueId;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(ContextCompat.getColor(context, R.color.problem_address));
        }

        @Override
        public void onClick(View view) {
            Intent intent = ProblemDetailActivity.getStartActivityIntent(
                    context,
                    issueId
            );

            ActivityCompat.startActivity(context, intent, null);
        }
    }
}
