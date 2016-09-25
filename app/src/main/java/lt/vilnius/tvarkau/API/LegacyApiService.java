package lt.vilnius.tvarkau.api;

import java.util.List;

import lt.vilnius.tvarkau.entity.LoginResponse;
import lt.vilnius.tvarkau.entity.Problem;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface LegacyApiService {

    @POST("server.php")
    Observable<ApiResponse<Problem>> getProblem(@Body ApiRequest<GetProblemParams> getProblemRequest);

    @POST("server.php")
    Observable<ApiResponse<List<Problem>>> getProblems(@Body ApiRequest<GetProblemsParams> getProblemsRequest);

    @POST("server.php")
    Observable<ApiResponse<Integer>> postNewProblem(@Body ApiRequest<GetNewProblemParams> getNewProblemRequest);

    @POST("server.php")
    Observable<ApiResponse<List<String>>> getProblemTypes(@Body ApiRequest<GetProblemTypesParams> getProblemTypesRequest);

    @POST("server.php")
    Observable<ApiResponse<LoginResponse>> loginToVilniusAccount(@Body ApiRequest<GetVilniusSignParams> getVilniusSignRequest);

    // logoutUser
    // registerUser
}
