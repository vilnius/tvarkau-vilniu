package lt.vilnius.tvarkau.mvp.interactors

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import lt.vilnius.tvarkau.analytics.Analytics
import lt.vilnius.tvarkau.backend.ApiMethod.NEW_PROBLEM
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetNewProblemParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.fragments.interactors.MyReportsInteractor
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import java.io.File

/**
 * @author Martynas Jurkus
 */
class NewReportInteractorImpl(
        private val legacyApiService: LegacyApiService,
        private val myReportsInteractor: MyReportsInteractor,
        private val photoConverter: ReportPhotoProvider,
        private val ioScheduler: Scheduler,
        private val reportDescriptionTemplate: String,
        private val analytics: Analytics
) : NewReportInteractor {

    override fun submitReport(data: NewReportData): Single<String> {
        return Single.zip(
                preparePhotos(data.photoUrls),
                prepareReport(data),
                BiFunction { photos: Array<String>, builder: GetNewProblemParams.Builder ->
                    val p = if (photos.isEmpty()) null else photos
                    builder.setPhoto(p).create()
                }
        )
                .flatMap { legacyApiService.postNewProblem(ApiRequest(NEW_PROBLEM, it)) }
                .map { it.result.toString() }
                .doOnSuccess { myReportsInteractor.saveReportId(it) }
                .doOnSuccess {
                    analytics.trackReportRegistration(data.reportType, data.photoUrls.size)
                }
                .subscribeOn(ioScheduler)
    }

    private fun prepareReport(data: NewReportData): Single<GetNewProblemParams.Builder> {
        val description = if (data.dateTime.isNullOrBlank()) {
            data.description
        } else {
            reportDescriptionTemplate.format(data.description, data.dateTime, data.licencePlate)
        }

        return Single.just(
                GetNewProblemParams.Builder()
                        .setDescription(description)
                        .setType(data.reportType)
                        .setAddress(data.address)
                        .setLatitude(data.latitude!!)
                        .setLongitude(data.longitude!!)
                        .setEmail(data.email.emptyToNull())
                        .setPhone(data.phone.emptyToNull())
                        .setNameOfReporter(data.name.emptyToNull())
                        .setDateOfBirth(data.personalCode.emptyToNull())
        )
    }

    private fun preparePhotos(photoUrls: List<File>): Single<Array<String>> {
        return try {
            Single.just(photoUrls.map { photoConverter.convert(it) }.toTypedArray())
        } catch (e: Throwable) {
            Single.error(e)
        }
    }

    private fun String?.emptyToNull() = if (this.isNullOrBlank()) null else this
}