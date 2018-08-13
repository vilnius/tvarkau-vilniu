package lt.vilnius.tvarkau.dagger

/**
 * @author Martynas Jurkus
 */

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class IoScheduler
