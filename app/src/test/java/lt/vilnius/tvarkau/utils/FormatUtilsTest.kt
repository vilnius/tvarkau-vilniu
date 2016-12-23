package lt.vilnius.tvarkau.utils

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * @author Martynas Jurkus
 */
class FormatUtilsTest {

    @Test
    fun formatExif_invalidFormat() {
        assertNull(FormatUtils.formatExifAsLocalDateTime("2010-10-10 00:00:00"))
    }

    @Test
    fun formatExif_validFormat() {
        val result = FormatUtils.formatExifAsLocalDateTime("2010:10:10 12:34:56")

        assertEquals("2010-10-10 12:34", result)
    }
}