package lt.vilnius.tvarkau.entity

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * @author Martynas Jurkus
 */
class ProblemTest {

    @Test
    fun getPhotos_photosExist() {
        val problem = Problem(
                photo = listOf("photo_1", "photo_2")
        )

        assertEquals(2, problem.photos?.size)
    }

    @Test
    fun getPhotos_onlyThumbnail() {
        val problem = Problem(
                thumbnail = "thumb"
        )

        assertEquals(1, problem.photos?.size)
    }

    @Test
    fun getPhotos_doesNotExist() {
        val problem = Problem()

        assertNull(problem.photos)
    }
}