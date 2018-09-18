package lt.vilnius.tvarkau.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvarkauMiestaApi {

    @GET("cities")
    fun getCities(): Single<CitiesResponse>

    @GET("reports")
    fun getReports(
        @Query("type") type: Int? = null,
        @Query("status") status: Int? = null,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int
    ): Single<ReportsResponse>

    @GET("report_types")
    fun getReportTypes(): Single<ReportTypeResponse>

    @GET("report_statuses")
    fun getReportStatuses(): Single<ReportStatusesResponse>

    @GET("/reports/{report_id}")
    fun getReport(@Path("report_id") reportId: String): Single<ReportResponse>
}
