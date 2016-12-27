package lt.vilnius.tvarkau.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * @author Martynas Jurkus
 */
@RunWith(RobolectricTestRunner::class)
class TextUtilTest {

    @Test
    fun findSingleProblemId() {
        val single = "We have a huge problem here E50-7476/16(3.2.47E-SM4)."
        val problemIdOccurrences = TextUtils.findProblemIdOccurrences(single)

        assertEquals(1, problemIdOccurrences.size.toLong())
        assertEquals("E50-7476/16(3.2.47E-SM4)", problemIdOccurrences[0])
    }

    @Test
    fun findMultipleProblemIds() {
        val multiple = "We have tow huge problems this one E50-7476/16(3.2.47E-SM4) and " +
                "this one E50-111111/16(3.2.47E-SM4)."
        val problemIdOccurrences = TextUtils.findProblemIdOccurrences(multiple)

        assertEquals(2, problemIdOccurrences.size.toLong())
        assertEquals("E50-7476/16(3.2.47E-SM4)", problemIdOccurrences[0])
        assertEquals("E50-111111/16(3.2.47E-SM4)", problemIdOccurrences[1])
    }

    @Test
    fun skipMalformedProblemIds() {
        val single = "Malformed E507476/16(3.2.47E-SM4) and  E50-7476/16(3.2.47E-SM4."
        val problemIdOccurrences = TextUtils.findProblemIdOccurrences(single)

        assertTrue(problemIdOccurrences.isEmpty())
    }

    @Test
    fun includeLowerCaseProblemIds() {
        val single = "We have a huge problem here e50-7476/16(3.2.47E-SM4)."
        val problemIdOccurrences = TextUtils.findProblemIdOccurrences(single)

        assertEquals(1, problemIdOccurrences.size.toLong())
    }

    @Test
    fun checkNonGreedy() {
        val text = "Two sameE50-7476/16(3.2.47E-SM4) ids zzzE50-7476/16(3.2.47E-SM4))))"
        val occurrences = TextUtils.findProblemIdOccurrences(text)

        kotlin.test.assertEquals("E50-7476/16(3.2.47E-SM4)", occurrences[0])
        kotlin.test.assertEquals("E50-7476/16(3.2.47E-SM4)", occurrences[1])
    }
}
