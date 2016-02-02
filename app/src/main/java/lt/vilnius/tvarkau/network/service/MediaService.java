package lt.vilnius.tvarkau.network.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public interface MediaService {

    /**
     * Upload new media
     */
    @Multipart
    @POST("media")
    Call<Response> uploadMedia(@Part("file") RequestBody file);

    /**
     * Get Media information
     */
    @GET("media/{media_id}")
    Call<Response> getMediaInfo(@Path("media_id") int mediaId);

}
