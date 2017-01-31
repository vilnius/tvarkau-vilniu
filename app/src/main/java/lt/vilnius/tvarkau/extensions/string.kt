package lt.vilnius.tvarkau.extensions

/**
 * @author Martynas Jurkus
 */

fun String.emptyToNull(): String? {
    return if (this.isNullOrEmpty()) null else this
}

fun String?.nullToEmpty(): String {
    return if (this == null) "" else this
}