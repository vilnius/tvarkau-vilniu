package lt.vilnius.tvarkau.api

import io.reactivex.Single
import retrofit2.http.GET

interface TvarkauMiestaApi {

    @GET("cities")
    fun getCities(): Single<CitiesResponse>
}
