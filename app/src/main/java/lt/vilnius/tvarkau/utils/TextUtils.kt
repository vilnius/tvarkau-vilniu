package lt.vilnius.tvarkau.utils

import kotlin.text.RegexOption.IGNORE_CASE

/**
 * @author Martynas Jurkus
 */

object TextUtils {

    private val PROBLEM_ID_REGEXP = "[A-Z]\\d{2}-\\d+\\/\\d+\\(\\S+?\\)".toRegex(IGNORE_CASE)

    fun findReportIdOccurrences(source: String): List<String> {
        return PROBLEM_ID_REGEXP.findAll(source).map { it.value }.toList()
    }
}
