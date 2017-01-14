package lt.vilnius.tvarkau.utils

/**
 * See <a href="https://lt.wikipedia.org/wiki/Asmens_kodas">WIKI entry for details</a>
 *
 * @author Martynas Jurkus
 */
object PersonalCodeValidator {

    fun validate(personalCode: String): Boolean {
        if (personalCode.length != 11) return false
        if (personalCode.map(Char::isDigit).any { false }) return false

        val numbers = personalCode.map { Character.getNumericValue(it) }
        val control = numbers.last()

        var sum = numbers.take(10).mapIndexed { index, number ->
            FIRST_ITERATION_MULTIPLIERS[index] * number
        }.sum()

        var checksum = sum % 11
        if (checksum != 10 && checksum == control) return true

        sum = numbers.take(10).mapIndexed { index, number ->
            SECOND_ITERATION_MULTIPLIERS[index] * number
        }.sum()

        checksum = sum % 11 % 10

        return checksum == control
    }

    val FIRST_ITERATION_MULTIPLIERS = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 1)
    val SECOND_ITERATION_MULTIPLIERS = arrayOf(3, 4, 5, 6, 7, 8, 9, 1, 2, 3)
}