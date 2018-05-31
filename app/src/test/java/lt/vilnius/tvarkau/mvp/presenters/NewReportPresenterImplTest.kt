package lt.vilnius.tvarkau.mvp.presenters

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.fragments.NewReportFragment
import lt.vilnius.tvarkau.mvp.interactors.NewReportInteractor
import lt.vilnius.tvarkau.mvp.interactors.PersonalDataInteractor
import lt.vilnius.tvarkau.mvp.views.NewReportView
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import org.junit.Test

/**
 * @author Martynas Jurkus
 */
class NewReportPresenterImplTest {

    val interactor = mock<NewReportInteractor>()
    val view = mock<NewReportView>()
    val personalDataInteractor = mock<PersonalDataInteractor>()
    val analytics = mock<Analytics>()

    val fixture: NewReportPresenter by lazy {
        NewReportPresenterImpl(
                interactor,
                personalDataInteractor,
                view,
                Schedulers.trampoline(),
                analytics
        )
    }

    @Test
    fun submitProblem_success() {
        whenever(interactor.submitReport(any())).thenReturn(Single.just("1"))
        val validator = FieldAwareValidator.of(NewReportData())

        fixture.submitProblem(validator)

        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).showSuccess()
    }

    @Test
    fun submitProblem_validatorFails_errorDisplayed() {
        val data = NewReportData()
        val validator = FieldAwareValidator.of(data)
                .validate({ false }, 1, "Validation message")

        fixture.submitProblem(validator)

        verify(view).showValidationError(any())
        verifyZeroInteractions(interactor)
    }

    @Test
    fun submitProblem_interactorError_errorDisplayed() {
        whenever(interactor.submitReport(any())).thenReturn(Single.error(RuntimeException("Some error")))
        val validator = FieldAwareValidator.of(NewReportData())

        fixture.submitProblem(validator)

        verify(view).showError(any())
    }

    @Test
    fun submitProblem_success_PersonalDataNotUpdated() {
        whenever(interactor.submitReport(any())).thenReturn(Single.just("1"))
        val validator = FieldAwareValidator.of(NewReportData())

        fixture.submitProblem(validator)

        verifyZeroInteractions(personalDataInteractor)
    }

    @Test
    fun submitProblem_trafficViolation_success_updatePersonalData() {
        whenever(interactor.submitReport(any())).thenReturn(Single.just("1"))
        val validator = FieldAwareValidator.of(NewReportData(
                reportType = NewReportFragment.PARKING_VIOLATIONS,
                email = "test@email.com",
                personalCode = "38411111111"
        ))

        fixture.submitProblem(validator)

        verify(view).showSuccess()
        verify(personalDataInteractor).storePersonalData(any())
    }

    @Test
    fun submitProblem_validationFail_trackingPerformed() {
        val data = NewReportData()
        val validator = FieldAwareValidator.of(data)
                .validate({ false }, 1, "Validation message")

        fixture.submitProblem(validator)

        verify(analytics).trackReportValidation(eq("Validation message"))
    }
}