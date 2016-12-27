package lt.vilnius.tvarkau.utils

import java.util.*
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

/**
 * @author Martynas Jurkus
 */

object TextUtils {

    private val PROBLEM_ID_REGEXP = "[A-Z]\\d{2}-\\d+\\/\\d+\\(\\S+?\\)"
    private val PROBLEM_ID_PATTERN = Pattern.compile(PROBLEM_ID_REGEXP, CASE_INSENSITIVE)

    fun findProblemIdOccurrences(source: String): List<String> {
        val result = ArrayList<String>()
        val matcher = PROBLEM_ID_PATTERN.matcher(source)

        while (matcher.find()) {
            result.add(matcher.group())
        }

        return result
    }
}
