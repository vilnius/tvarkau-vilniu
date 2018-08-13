object Versions {
    const val kotlin = "1.2.31"
    const val fabric = "1.25.1"
    const val build_tools = "3.1.2"
    const val rx_java_2 = "2.1.10"
    const val robolectric = "3.7.1"
    const val dagger = "2.16"
    const val lifecycle = "1.1.1"
    const val retrofit = "2.3.0"
    const val leak_canary = "1.5.4"
    const val butterknife = "8.8.1"
    const val gson = "2.8.4"
    const val timber = "4.7.0"
    const val stetho = "1.5.0"
    const val okhttp = "3.10.0"
    const val rxjava = "1.3.8"
    const val rxandroid = "1.2.1"
    const val mixpanel = "5.1.4"
    const val three_ten = "1.1.0"
    const val photo_view = "1.3.1"
    const val event_bus = "3.0.0"
    const val glide = "3.8.0"
    const val view_page_indicator = "2.4.1.1@aar"
    const val javax_annotation = "1.0"
    const val easy_image = "2.0.4"
    const val oauth = "2.4.2"
    const val preferx = "1.1.0"

    const val support = "27.1.1"
    const val google_services = "3.3.1"
    const val play_services = "15.0.1"
    const val firebase = "15.0.2"
    const val firebase_perf = "15.2.0"
    const val firebase_plugins = "1.1.1"
    const val crashlytics = "2.9.2"

    const val junit = "4.12"
    const val assertj_core = "3.9.1"
    const val assertj_android = "1.1.1"
    const val mockito_kotlin = "1.5.0"
    const val fest_util = "1.2.5"
    const val fest_reflect = "1.4.1"

    const val compileSdkVersion = 27
    const val minSdkVersion = 16
    const val targetSdkVersion = 26

    private const val major = 4
    private const val minor = 1
    private const val patch = 0
    private const val micro = 0

    const val versionCode: Int = major * 1000000 + minor * 10000 + patch * 100 + micro
    const val versionString: String = "$major.$minor.$patch.$micro"
}

object Libs {
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val dagger_compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val javax_annotation = "javax.annotation:jsr250-api:${Versions.javax_annotation}"

    const val view_page_indicator = "fr.avianey.com.viewpagerindicator:library:${Versions.view_page_indicator}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    const val event_bus = "org.greenrobot:eventbus:${Versions.event_bus}"

    const val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
    const val stetho_okhttp3 = "com.facebook.stetho:stetho-okhttp3:${Versions.stetho}"

    const val mixpanel = "com.mixpanel.android:mixpanel-android:${Versions.mixpanel}"

    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    const val rx_android = "io.reactivex:rxandroid:${Versions.rxandroid}"
    const val rx_java = "io.reactivex:rxjava:${Versions.rxjava}"
    const val rx_java_2 = "io.reactivex.rxjava2:rxjava:${Versions.rx_java_2}"

    const val three_ten = "com.jakewharton.threetenabp:threetenabp:${Versions.three_ten}"

    const val photo_view = "com.github.chrisbanes:PhotoView:${Versions.photo_view}"

    const val maps = "com.google.android.gms:play-services-maps:${Versions.play_services}"
    const val location = "com.google.android.gms:play-services-location:${Versions.play_services}"
    const val places = "com.google.android.gms:play-services-places:${Versions.play_services}"

    const val firebase_core = "com.google.firebase:firebase-core:${Versions.firebase}"
    const val firebase_perf = "com.google.firebase:firebase-perf:${Versions.firebase_perf}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofit_adapter_rx = "com.squareup.retrofit2:adapter-rxjava:${Versions.retrofit}"

    const val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"

    const val leak_canary = "com.squareup.leakcanary:leakcanary-android:${Versions.leak_canary}"
    const val leak_canary_no_op = "com.squareup.leakcanary:leakcanary-android-no-op:${Versions.leak_canary}"

    const val butterknife = "com.jakewharton:butterknife:${Versions.butterknife}"
    const val butterknife_compiler = "com.jakewharton:butterknife-compiler:${Versions.butterknife}"

    const val easy_image = "com.github.jkwiecien:EasyImage:${Versions.easy_image}"

    const val oauth = "ca.mimic:oauth2library:${Versions.oauth}"

    const val preferx = "com.vinted:preferx:${Versions.preferx}"
}

object SupportLibraries {
    const val fragment = "com.android.support:support-fragment:${Versions.support}}"
    const val appcompat_v7 = "com.android.support:appcompat-v7:${Versions.support}}"
    const val design = "com.android.support:design:${Versions.support}"
    const val recyclerview_v7 = "com.android.support:recyclerview-v7:${Versions.support}"
    const val preference_v14 = "com.android.support:preference-v14:${Versions.support}"
}

object TestLibraries {
    const val junit = "junit:junit:${Versions.junit}"
    const val assertj_core = "org.assertj:assertj-core:${Versions.assertj_core}"
    const val assertj_android = "com.squareup.assertj:assertj-android:${Versions.assertj_android}"
    const val mockito_kotlin = "com.nhaarman:mockito-kotlin:${Versions.mockito_kotlin}"
    const val lifecycle_testing = "android.arch.core:core-testing:${Versions.lifecycle}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val kotlin_test = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    const val fest_util = "org.easytesting:fest-util:${Versions.fest_util}"
    const val fest_reflect = "org.easytesting:fest-reflect:${Versions.fest_reflect}"
}
