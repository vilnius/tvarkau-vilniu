package lt.vilnius.tvarkau.backend;

import java.util.List;

import lt.vilnius.tvarkau.backend.requests.GetReportRequest;
import lt.vilnius.tvarkau.backend.requests.GetReportTypesRequest;
import lt.vilnius.tvarkau.entity.LoginResponse;
import lt.vilnius.tvarkau.entity.Problem;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import rx.Single;

public interface LegacyApiService {

    @POST("server.php")
    Observable<ApiResponse<Problem>> getProblem(@Body GetReportRequest getProblemRequest);

    @POST("server.php")
    Observable<ApiResponse<List<Problem>>> getProblems(@Body ApiRequest<GetProblemsParams> getProblemsRequest);

    @POST("server.php")
    Observable<ApiResponse<Integer>> postNewProblem(@Body ApiRequest<GetNewProblemParams> getNewProblemRequest);

    @POST("server.php")
    Single<ApiResponse<List<String>>> getProblemTypes(@Body GetReportTypesRequest request);

    @POST("server.php")
    Observable<ApiResponse<LoginResponse>> loginToVilniusAccount(@Body ApiRequest<GetVilniusSignParams> getVilniusSignRequest);
}
