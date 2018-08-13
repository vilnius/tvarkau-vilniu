package lt.vilnius.tvarkau.backend;

import java.util.List;

import io.reactivex.Single;
import lt.vilnius.tvarkau.backend.requests.GetReportRequest;
import lt.vilnius.tvarkau.backend.requests.GetReportTypesRequest;
import lt.vilnius.tvarkau.entity.LoginResponse;
import lt.vilnius.tvarkau.entity.Problem;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LegacyApiService {

    @POST("server.php")
    Single<ApiResponse<Problem>> getProblem(@Body GetReportRequest getProblemRequest);

    @POST("server.php")
    Single<ApiResponse<List<Problem>>> getProblems(@Body ApiRequest<GetProblemsParams> getProblemsRequest);

    @POST("server.php")
    Single<ApiResponse<Integer>> postNewProblem(@Body ApiRequest<GetNewProblemParams> getNewProblemRequest);

    @POST("server.php")
    Single<ApiResponse<List<String>>> getProblemTypes(@Body GetReportTypesRequest request);

    @POST("server.php")
    Single<ApiResponse<LoginResponse>> loginToVilniusAccount(@Body ApiRequest<GetVilniusSignParams> getVilniusSignRequest);
}
