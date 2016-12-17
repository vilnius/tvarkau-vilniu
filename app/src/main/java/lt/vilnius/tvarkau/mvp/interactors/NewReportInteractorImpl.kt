package lt.vilnius.tvarkau.mvp.interactors

import lt.vilnius.tvarkau.backend.ApiMethod.NEW_PROBLEM
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetNewProblemParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.mvp.presenters.NewReportData
import rx.Observable
import rx.Scheduler
import rx.Single

/**
 * @author Martynas Jurkus
 */
class NewReportInteractorImpl(
        val legacyApiService: LegacyApiService,
        val photoConverter: ReportPhotoProvider,
        val ioScheduler: Scheduler
) : NewReportInteractor {

    override fun submitReport(data: NewReportData): Single<String> {

        return Observable.from(data.photoUrls)
                .map { photoConverter.convert(it) }
                .toList()
                .map { it.toTypedArray() }
                .map { if (it.isEmpty()) null else it }
                .flatMap {
                    val params = GetNewProblemParams.Builder()
                            .setDescription(data.description)
                            .setType(data.reportType)
                            .setAddress(data.address)
                            .setLatitude(data.latitude!!)
                            .setLongitude(data.longitude!!)
                            .setPhoto(it)
                            .setEmail(data.email.emptyToNull())
                            .setPhone(data.phone.emptyToNull())
                            .setNameOfReporter(data.name.emptyToNull())
                            .setDateOfBirth(data.dateOfBirth.emptyToNull())
                            .create()

                    legacyApiService.postNewProblem(ApiRequest(NEW_PROBLEM, params))
                }
                .map { it.result.toString() }
                .subscribeOn(ioScheduler)
                .toSingle()
    }

    fun String?.emptyToNull() = if (this.isNullOrBlank()) null else this
}