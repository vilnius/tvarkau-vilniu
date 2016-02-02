package lt.vilnius.tvarkau.network.service;

import lt.vilnius.tvarkau.entity.IssuesResponse;
import lt.vilnius.tvarkau.entity.NewIssueRequest;
import lt.vilnius.tvarkau.entity.Problem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public interface IssueService {
    /**
     * Lists all issues
     */
    @GET("issues")
    Call<IssuesResponse> getIssues();

    /**
     * Shows details of a single issue
     */
    @GET("issue/{issue_id}")
    Call<Problem> getIssue(@Path("issue_id") int issueId);


    /**
     * Creates a new issue.
     * Media attachments have to be uploaded before-hand.
     */
    @POST("issues")
    Call<IssuesResponse> createIssue(@Body NewIssueRequest newIssueRequest);
}
