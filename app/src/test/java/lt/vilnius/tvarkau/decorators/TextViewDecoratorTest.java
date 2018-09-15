package lt.vilnius.tvarkau.decorators;

import android.text.Spannable;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import lt.vilnius.tvarkau.decorators.TextViewDecorator.ReportReferenceNumberSpan;

import static org.junit.Assert.assertEquals;

/**
 * @author Martynas Jurkus
 */
@RunWith(RobolectricTestRunner.class)
public class TextViewDecoratorTest {

    private TextView textView;
    private String text = "Two same E50-7476/16(3.2.47E-SM4) ids E50-7476/16(3.2.47E-SM4)";

    @Before
    public void setUp() {
        textView = new TextView(RuntimeEnvironment.application);
    }

    @Test
    public void matchingProblemIds_containsTwoSpans() {
        new TextViewDecorator(textView).decorateProblemIdSpans(text);
        Spannable spannable = (Spannable) textView.getText();

        ReportReferenceNumberSpan[] spans = spannable.getSpans(0, text.length(), ReportReferenceNumberSpan.class);

        assertEquals(2, spans.length);
    }

    @Test
    public void matchingProblemIds_verifySpanPositions() {
        new TextViewDecorator(textView).decorateProblemIdSpans(text);
        Spannable spannable = (Spannable) textView.getText();

        ReportReferenceNumberSpan[] spans = spannable.getSpans(0, text.length(), ReportReferenceNumberSpan.class);

        assertEquals(9, spannable.getSpanStart(spans[0]));
        assertEquals(33, spannable.getSpanEnd(spans[0]));

        assertEquals(38, spannable.getSpanStart(spans[1]));
        assertEquals(62, spannable.getSpanEnd(spans[1]));
    }
}
