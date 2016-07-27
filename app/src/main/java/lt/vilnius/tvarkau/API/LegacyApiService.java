package lt.vilnius.tvarkau.API;

import java.util.List;

import lt.vilnius.tvarkau.entity.Problem;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface LegacyApiService {

    @POST("server.php")
    Observable<ApiResponse<Problem>> getProblem(@Body ApiRequest<GetProblemParams> getProblemRequest);

    @POST("server.php")
    Observable<ApiResponse<List<Problem>>> getProblems(@Body ApiRequest<GetProblemsParams> getProblemsRequest);

    // postNewProblem
    // getProblemTypes

    // loginUser
    // logoutUser
    // registerUser
}
