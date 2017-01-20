package lt.vilnius.tvarkau.mvp.interactors

import com.nhaarman.mockito_kotlin.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.ApiResponse
import lt.vilnius.tvarkau.backend.GetNewProblemParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.base.BaseRobolectricTest
import lt.vilnius.tvarkau.fragments.interactors.MyReportsInteractor
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import org.junit.Test
import rx.Observable.just
import rx.schedulers.Schedulers
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * @author Martynas Jurkus
 */
class NewReportInteractorImplTest : BaseRobolectricTest() {

    val api = mock<LegacyApiService>()
    val photoProvider = mock<ReportPhotoProvider>()
    val analytics = mock<Analytics>()
    val myReportsInteractor = mock<MyReportsInteractor>()

    val fixture: NewReportInteractor by lazy {
        NewReportInteractorImpl(
                api,
                myReportsInteractor,
                photoProvider,
                Schedulers.immediate(),
                activity.getString(R.string.report_description_timestamp_template),
                analytics
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

    @Test
    fun submit_withDateTime_descriptionFormatted() {
        val report = NewReportData(
                description = "sample",
                dateTime = "2020-10-10 10:00",
                licencePlate = "AAA111",
                latitude = 0.0,
                longitude = 0.0,
                photoUrls = emptyList()
        )

        val captor = argumentCaptor<ApiRequest<GetNewProblemParams>>()
        whenever(api.postNewProblem(captor.capture())).thenReturn(just(successResponse))

        fixture.submitReport(report)
                .test()
                .assertNoErrors()
                .assertValue("1")

        verify(api).postNewProblem(any())
        verify(photoProvider, never()).convert(any())

        val description = captor.firstValue.params.description

        assertTrue { description.contains("2020-10-10 10:00") }
        assertTrue { description.contains("AAA111") }
    }

    @Test
    fun submit_wthoutDateTime_useOriginalDescription() {
        val report = NewReportData(
                description = "sample",
                latitude = 0.0,
                longitude = 0.0,
                photoUrls = emptyList()
        )

        val captor = argumentCaptor<ApiRequest<GetNewProblemParams>>()
        whenever(api.postNewProblem(captor.capture())).thenReturn(just(successResponse))

        fixture.submitReport(report)
                .test()
                .assertNoErrors()
                .assertValue("1")

        verify(api).postNewProblem(any())
        verify(photoProvider, never()).convert(any())

        val description = captor.firstValue.params.description

        assertEquals(report.description, description)
    }

    @Test
    fun submitSuccess_trackingPerformed() {
        val type = "some_type"
        val emptyReport = NewReportData(
                reportType = type,
                latitude = 0.0,
                longitude = 0.0,
                photoUrls = listOf(File("one"), File("two"))
        )

        whenever(api.postNewProblem(any())).thenReturn(just(successResponse))

        fixture.submitReport(emptyReport).subscribe()

        verify(analytics).trackReportRegistration(type, 2)
    }

    @Test
    fun submitSuccess_myReportIdStored() {
        val type = "some_type"
        val emptyReport = NewReportData(
                reportType = type,
                latitude = 0.0,
                longitude = 0.0
        )

        whenever(api.postNewProblem(any())).thenReturn(just(successResponse))

        fixture.submitReport(emptyReport).subscribe()

        verify(myReportsInteractor).saveReportId("1")
    }
}