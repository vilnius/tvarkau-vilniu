package lt.vilnius.tvarkau;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import lt.vilnius.tvarkau.utils.TextUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Martynas Jurkus
 */
@RunWith(RobolectricTestRunner.class)
public class TextUtilTest {

    @Test
    public void findSingleProblemId() {
        String single = "We have a huge problem here E50-7476/16(3.2.47E-SM4).";
        List<String> problemIdOccurrences = TextUtils.findProblemIdOccurrences(single);

        assertEquals(1, problemIdOccurrences.size());
        assertEquals("E50-7476/16(3.2.47E-SM4)", problemIdOccurrences.get(0));
    }

    @Test
    public void findMultipleProblemIds() {
        String multiple = "We have tow huge problems this one E50-7476/16(3.2.47E-SM4) and " +
                "this one E50-111111/16(3.2.47E-SM4).";
        List<String> problemIdOccurrences = TextUtils.findProblemIdOccurrences(multiple);

        assertEquals(2, problemIdOccurrences.size());
        assertEquals("E50-7476/16(3.2.47E-SM4)", problemIdOccurrences.get(0));
        assertEquals("E50-111111/16(3.2.47E-SM4)", problemIdOccurrences.get(1));
    }

    @Test
    public void skipMalformedProblemIds() {
        String single = "Malformed E507476/16(3.2.47E-SM4) and  E50-7476/16(3.2.47E-SM4.";
        List<String> problemIdOccurrences = TextUtils.findProblemIdOccurrences(single);

        assertTrue(problemIdOccurrences.isEmpty());
    }
}
