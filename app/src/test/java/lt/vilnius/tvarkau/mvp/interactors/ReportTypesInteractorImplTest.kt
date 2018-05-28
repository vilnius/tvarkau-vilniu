package lt.vilnius.tvarkau.mvp.interactors

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.wrapInResponse
import org.junit.Test

/**
 * @author Martynas Jurkus
 */
class ReportTypesInteractorImplTest {

    val api = mock<LegacyApiService>()
    val fixture: ReportTypesInteractor = ReportTypesInteractorImpl(
            api,
            Schedulers.trampoline()
    )

    @Test
    fun emptyCache_apiHit() {
        val values = listOf("Type 1", "Type 2")

        whenever(api.getProblemTypes(any()))
                .thenReturn(values.wrapInResponse)

        fixture.getReportTypes()
                .test()
                .assertValue(values)

        verify(api).getProblemTypes(any())
    }

    @Test
    fun cacheHit_noApiRequest() {
        val values = listOf("Type 1", "Type 2")

        whenever(api.getProblemTypes(any()))
                .thenReturn(values.wrapInResponse)
                .thenReturn(Single.error(IllegalStateException("Unexpected API call")))

        fixture.getReportTypes().subscribe()

        fixture.getReportTypes()
                .test()
                .assertNoErrors()
                .assertValue(values)
    }

    @Test
    fun emptyCache_emptyApiResult_errorProduced() {
        whenever(api.getProblemTypes(any()))
                .thenReturn(listOf<String>().wrapInResponse)

        fixture.getReportTypes()
                .test()
                .assertError(IllegalStateException::class.java)
    }
}