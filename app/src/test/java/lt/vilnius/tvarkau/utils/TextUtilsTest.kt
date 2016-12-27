package lt.vilnius.tvarkau.utils

import org.junit.Test
import kotlin.test.assertEquals

/**
 * @author Martynas Jurkus
 */
class TextUtilsTest {

    private val text = "Two sameE50-7476/16(3.2.47E-SM4) ids zzzE50-7476/16(3.2.47E-SM4))))"

    @Test
    fun findProblemIds() {
        val occurrences = TextUtils.findProblemIdOccurrences(text)

        assertEquals("E50-7476/16(3.2.47E-SM4)", occurrences[0])
        assertEquals("E50-7476/16(3.2.47E-SM4)", occurrences[1])
    }
}