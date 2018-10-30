package lt.vilnius.tvarkau.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface TvarkauMiestaApi {

    @GET("me")
    fun getCurrentUser(): Single<UserResponse>

    @GET("cities")
    fun getCities(): Single<CitiesResponse>

    @GET("reports")
    fun getReports(@QueryMap params: Map<String, String>): Single<ReportsResponse>

    @POST("reports")
    fun submitReport(@Body body: NewReportRequest): Single<ReportResponse>

    @GET("report_types")
    fun getReportTypes(): Single<ReportTypeResponse>

    @GET("report_statuses")
    fun getReportStatuses(): Single<ReportStatusesResponse>

    @GET("/reports/{report_id}")
    fun getReport(@Path("report_id") reportId: String): Single<ReportResponse>
}
