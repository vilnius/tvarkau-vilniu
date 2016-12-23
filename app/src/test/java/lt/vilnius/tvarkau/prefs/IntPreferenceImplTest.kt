package lt.vilnius.tvarkau.prefs

import lt.vilnius.tvarkau.prefs.IntPreferenceImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import rx.observers.TestSubscriber
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class IntPreferenceImplTest {
    val prefs = RuntimeEnvironment.application.getSharedPreferences("foo", 0)
    val key = "foo"
    val default = 123
    val fixture = IntPreferenceImpl(prefs, key, default)
    val testSubscriber = TestSubscriber<Int>()

    @Test
    fun testGet_noSavedValue_getDefault() {
        val value = fixture.get()

        assertEquals(default, value)
    }

    @Test
    fun testGet_savedValue_getSavedValue() {
        prefs.edit().putInt(key, 11).commit()

        val value = fixture.get()

        assertEquals(11, value)
    }

    @Test
    fun testGet_saveValueInWrongType_getDefault() {
        prefs.edit().putString(key, "bar").commit()

        val value = fixture.get()

        assertEquals(default, value)
    }

    @Test
    fun testSet_valueIsSaved() {
        fixture.set(99)

        assertEquals(99, prefs.getInt(key, 0))
    }

    @Test
    fun testSet_saveValue_overrideOldValue() {
        prefs.edit().putInt(key, 11).commit()

        fixture.set(99)

        assertEquals(99, prefs.getInt(key, 0))
    }

    @Test
    fun testDelete_noValue_getDefault() {
        fixture.delete()

        assertEquals(default, fixture.get())
    }

    @Test
    fun testDelete_savedValue_getDefault() {
        prefs.edit().putInt(key, 11).commit()

        fixture.delete()

        assertEquals(default, fixture.get())
    }

    @Test
    fun testIsSet_noValue_getFalse() {
        val isSet = fixture.isSet()

        assertFalse { isSet }
    }

    @Test
    fun testIsSet_savedValue_getTrue() {
        prefs.edit().putInt(key, 11).commit()

        val isSet = fixture.isSet()

        assertTrue { isSet }
    }

    @Test
    fun testIsSet_saveValue_getTrue() {
        fixture.set(222)

        val isSet = fixture.isSet()

        assertTrue { isSet }
    }

    @Test
    fun testGet_putValueThroughAnotherPrefWithSameKey_getNewValue() {
        val another = IntPreferenceImpl(prefs, key, default)
        another.set(34)

        val value = fixture.get()

        assertEquals(34, value)
    }

    @Test
    fun testGet_getFromFixtureAndPutValueThroughPrefs_getNewValue() {
        val value1 = fixture.get()
        prefs.edit().putInt(key, 123).commit()

        val value2 = fixture.get()

        assertEquals(default, value1)
        assertEquals(123, value2)
    }
}