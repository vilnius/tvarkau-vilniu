package lt.vilnius.tvarkau.mvp.interactors

import android.content.Context
import lt.vilnius.tvarkau.utils.ImageUtils
import java.io.File

/**
 * @author Martynas Jurkus
 */
class ReportPhotoProviderImpl(
        private val context: Context
) : ReportPhotoProvider {

    override fun convert(file: File): String {
        return ImageUtils.convertToBase64EncodedString(context, file)
                ?: throw RuntimeException("Failed to convert photo ${file.path}")
    }
}