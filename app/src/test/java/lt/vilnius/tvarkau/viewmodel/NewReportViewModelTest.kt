package lt.vilnius.tvarkau.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.entity.ReportStatus
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractor
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import lt.vilnius.tvarkau.repository.ReportsRepository
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import lt.vilnius.tvarkau.utils.ProgressState
import org.junit.Rule
import org.junit.Test

class NewReportViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val personalDataInteractor: PersonalDataInteractor = mock()
    private val analytics: Analytics = mock()
    private val repository: ReportsRepository = mock()
    private val reportType = ReportType(id = 1, title = "Test type")

    private val fixture = NewReportViewModel(
        uiScheduler = Schedulers.trampoline(),
        personalDataInteractor = personalDataInteractor,
        analytics = analytics,
        repository = repository
    )

    @Test
    fun submitProblem_success() {
        val reportEntity = ReportEntity(
            id = 1,
            reportType = reportType,
            reportStatus = ReportStatus(id = 1, title = "Registered", color = "hex"),
            refNo = "ref_no",
            lat = 0.0,
            lng = 0.0,
            userId = 1,
            description = "description"
        )
        whenever(repository.submitReport(any())).thenReturn(Single.just(reportEntity))

        val progressObserver = mock<Observer<ProgressState>>()
        fixture.progressState.observeForever(progressObserver)

        val validator = FieldAwareValidator.of(NewReportData(reportType))
        fixture.submit(validator)

        verify(progressObserver, times(2)).onChanged(any())
    }

    @Test
    fun submitProblem_validatorFails_errorDisplayed() {
        val validator = FieldAwareValidator.of(NewReportData(reportType))
            .validate({ false }, 1, "Validation message")

        val validationObserver = mock<Observer<FieldAwareValidator.ValidationException>>()
        fixture.validationError.observeForever(validationObserver)

        fixture.submit(validator)

        verify(validationObserver).onChanged(any())
        verifyZeroInteractions(repository)
    }

    @Test
    fun submitProblem_interactorError_errorDisplayed() {
        val exception = RuntimeException("Some error")
        whenever(repository.submitReport(any())).thenReturn(Single.error(exception))
        val errorEventObserver = mock<Observer<Throwable>>()
        fixture.errorEvents.observeForever(errorEventObserver)

        val validator = FieldAwareValidator.of(NewReportData(reportType))
        fixture.submit(validator)

        verify(errorEventObserver).onChanged(exception)
    }

    @Test
    fun submitProblem_validationFail_trackingPerformed() {
        val validator = FieldAwareValidator.of(NewReportData(reportType))
            .validate({ false }, 1, "Validation message")

        fixture.submit(validator)

        verify(analytics).trackReportValidation(eq("Validation message"))
    }
}
