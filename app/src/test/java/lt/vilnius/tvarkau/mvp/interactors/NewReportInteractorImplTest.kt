package lt.vilnius.tvarkau.mvp.interactors

import com.nhaarman.mockito_kotlin.*
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.backend.GetNewProblemParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import org.junit.Test
import rx.Observable.just
import rx.schedulers.Schedulers
import java.io.File
import kotlin.test.assertNull

/**
 * @author Martynas Jurkus
 */
class NewReportInteractorImplTest {

    val api = mock<LegacyApiService>()
    val photoProvider = mock<ReportPhotoProvider>()

    val fixture: NewReportInteractor by lazy {
        NewReportInteractorImpl(
                api,
                photoProvider,
                Schedulers.immediate()
        )
    }

    val successResponse: ApiResponse<Int>
        get() {
            return ApiResponse<Int>().apply {
                result = 1
            }
        }

    @Test
    fun submit_noPhotos_success() {
        val emptyReport = NewReportData(
                address = "",
                description = "",
                reportType = "",
                latitude = 0.0,
                longitude = 0.0,
                photoUrls = emptyList()
        )

        val captor = argumentCaptor<ApiRequest<GetNewProblemParams>>()
        whenever(api.postNewProblem(captor.capture())).thenReturn(just(successResponse))

        fixture.submitReport(emptyReport)
                .test()
                .assertNoErrors()
                .assertValue("1")

        verify(api).postNewProblem(any())
        verify(photoProvider, never()).convert(any())

        val params = captor.firstValue.params
        assertNull(params.photo)
    }

    @Test
    fun submit_photoProviderThrowsError_errorReceived() {
        val emptyReport = NewReportData(
                address = "",
                description = "",
                reportType = "",
                latitude = 0.0,
                longitude = 0.0,
                photoUrls = listOf(File("1"))
        )

        whenever(photoProvider.convert(any())).thenThrow(RuntimeException("Failed to convert photo"))

        fixture.submitReport(emptyReport)
                .test()
                .assertError(RuntimeException::class.java)

        verify(api, never()).postNewProblem(any())
    }
}