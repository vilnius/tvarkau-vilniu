package lt.vilnius.tvarkau.mvp.presenters

import com.nhaarman.mockito_kotlin.*
import lt.vilnius.tvarkau.mvp.interactors.NewReportInteractor
import lt.vilnius.tvarkau.mvp.views.NewReportView
import lt.vilnius.tvarkau.utils.FieldAwareValidator
import org.junit.Test
import rx.Single
import rx.schedulers.Schedulers

/**
 * @author Martynas Jurkus
 */
class NewReportPresenterImplTest {

    val interactor = mock<NewReportInteractor>()
    val view = mock<NewReportView>()

    val fixture: NewReportPresenter by lazy {
        NewReportPresenterImpl(
                interactor,
                view,
                Schedulers.immediate()
        )
    }

    @Test
    fun submitProblem_success() {
        whenever(interactor.submitReport(any())).thenReturn(Single.just("1"))
        val validator = FieldAwareValidator.of(NewReportData())

        fixture.submitProblem(validator)

        verify(view).showProgress()
        verify(view).hideProgress()
        verify(view).showSuccess("1")
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
}