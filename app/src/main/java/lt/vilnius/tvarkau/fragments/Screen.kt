package lt.vilnius.tvarkau.fragments

import android.support.annotation.StringRes


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Screen(
        @StringRes val titleRes: Int = 0,
        val navigationMode: NavigationMode = NavigationMode.DEFAULT,
        val trackingScreenName: String = ""
)

enum class NavigationMode {
    DEFAULT,
    CLOSE,
    BACK,
}