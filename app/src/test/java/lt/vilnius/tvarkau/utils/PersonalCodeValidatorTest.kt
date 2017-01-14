package lt.vilnius.tvarkau.utils

import org.junit.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author Martynas Jurkus
 */
class PersonalCodeValidatorTest {

    @Test
    fun too_short_fail() {
        assertFalse { PersonalCodeValidator.validate("1234567890") }
    }

    @Test
    fun too_long_fail() {
        assertFalse { PersonalCodeValidator.validate("1234567890123") }
    }

    @Test
    fun with_letters_fail() {
        assertFalse { PersonalCodeValidator.validate("12345z78901") }
    }

    @Test
    fun checksum_fails() {
        assertFalse { PersonalCodeValidator.validate("34508028193") }
    }

    @Test
    fun dataSet_all_pass() {
        val result = File(PersonalCodeValidatorTest::class.java.getResource(PERSONAL_CODES).toURI())
                .readLines()
                .map { PersonalCodeValidator.validate(it) to it }

        assertTrue { result.all { it.first } }
    }

    companion object {
        private const val PERSONAL_CODES = "personal_codes.txt"
    }
}