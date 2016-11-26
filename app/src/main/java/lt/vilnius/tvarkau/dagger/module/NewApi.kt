package lt.vilnius.tvarkau.dagger.module

/**
 * @author Martynas Jurkus
 */

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class NewApi
