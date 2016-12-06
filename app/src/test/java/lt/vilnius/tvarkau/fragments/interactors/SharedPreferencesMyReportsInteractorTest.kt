package lt.vilnius.tvarkau.fragments.interactors

import android.content.Context
import lt.vilnius.tvarkau.fragments.interactors.SharedPreferencesMyReportsInteractor.Companion.PROBLEM_PREFERENCE_KEY
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author Martynas Jurkus
 */
@RunWith(RobolectricTestRunner::class)
class SharedPreferencesMyReportsInteractorTest {

    val preferences by lazy {
        RuntimeEnvironment.application.getSharedPreferences("test", Context.MODE_PRIVATE)
    }

    val fixture by lazy {
        SharedPreferencesMyReportsInteractor(preferences)
    }

    @Test
    fun storeId_success() {
        fixture.saveReportId("1")

        assertTrue { preferences.contains("${PROBLEM_PREFERENCE_KEY}1") }
    }

    @Test
    fun returnMultipleResults() {
        fixture.saveReportId("1")
        fixture.saveReportId("2")
        fixture.saveReportId("3")

        val result = fixture.getReportIdsImmediate()

        assertTrue { result.containsAll(listOf("1", "2", "3")) }
    }

    @Test
    fun removeReportId_success() {
        fixture.saveReportId("1")

        fixture.removeReportId("1")

        assertFalse { preferences.contains("${PROBLEM_PREFERENCE_KEY}1") }
    }
}