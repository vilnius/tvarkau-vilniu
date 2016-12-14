package lt.vilnius.tvarkau.mvp.interactors

import java.io.File

/**
 * @author Martynas Jurkus
 */
interface ReportPhotoProvider {
    fun convert(file: File): String
}