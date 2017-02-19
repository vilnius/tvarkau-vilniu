package lt.vilnius.tvarkau.extensions

/**
 * @author Martynas Jurkus
 */

fun String.emptyToNull(): String? {
    return if (this.isNullOrEmpty()) null else this
}