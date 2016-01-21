package lt.vilnius.tvarkau.network.service;

import lt.vilnius.tvarkau.entity.Token;
import lt.vilnius.tvarkau.network.APIClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    @POST("/media")
    Call<Response> uploadMedia(@Header(APIClient.X_AUTH) Token token,
                               @Part("file") RequestBody file);

    /**
     * Get Media information
     */
    @GET("/media/{media_id}")
    Call<Response> getMediaInfo(@Header(APIClient.X_AUTH) Token token,
                                @Path("media_id") int mediaId);

}
