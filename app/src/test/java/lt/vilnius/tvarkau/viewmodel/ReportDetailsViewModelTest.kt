package lt.vilnius.tvarkau.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import lt.vilnius.tvarkau.entity.ReportEntity
import lt.vilnius.tvarkau.entity.ReportStatus
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.repository.ReportsRepository
import org.junit.Rule
import org.junit.Test


class ReportDetailsViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    val report = ReportEntity(
        id = 1,
        userId = 1,
        description = "description",
        lat = 1.1,
        lng = 2.2,
        reportStatus = ReportStatus(id = 1, title = "Registered", color = "#fff"),
        reportType = ReportType(id = 1, title = "Aplinkos tvarkymas")
    )

    private val reportsRepository = mock<ReportsRepository>()

    private val fixture = ReportDetailsViewModel(
        reportsRepository = reportsRepository,
        uiScheduler = Schedulers.trampoline()
    )

    @Test
    fun initialization() {
        whenever(reportsRepository.getReportById(any())).doReturn(Single.just(report))

        val observer = mock<Observer<ReportEntity>>()
        fixture.report.observeForever(observer)

        fixture.initWith(1)

        verify(observer).onChanged(report)
    }

    @Test
    fun errorHandling() {
        val exception = RuntimeException("DB Exception")
        whenever(reportsRepository.getReportById(any())).doReturn(Single.error(exception))

        val observer = mock<Observer<Throwable>>()
        fixture.errorEvents.observeForever(observer)

        fixture.initWith(1)

        verify(observer).onChanged(exception)
    }
}
