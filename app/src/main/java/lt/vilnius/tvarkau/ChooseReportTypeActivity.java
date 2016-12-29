package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.backend.ApiMethod;
import lt.vilnius.tvarkau.backend.ApiRequest;
import lt.vilnius.tvarkau.backend.ApiResponse;
import lt.vilnius.tvarkau.backend.GetProblemTypesParams;
import lt.vilnius.tvarkau.views.adapters.ReportTypesListAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static lt.vilnius.tvarkau.views.adapters.ReportTypesListAdapter.ReportTypeSelectedListener;

public class ChooseReportTypeActivity extends BaseActivity implements ReportTypeSelectedListener {

    public static final String EXTRA_REPORT_TYPE = "ChooseReportTypeActivity.reportType";

    @BindView(R.id.report_types_recycler_view)
    RecyclerView reportTypesRecyclerView;

    ReportTypesListAdapter reportTypesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_report_type);

        ButterKnife.bind(this);

        if (savedInstanceState == null)
            setReportTypesAdapter();
    }

    @Override
    protected void onStop() {
        if (reportTypesListAdapter != null) {
            reportTypesListAdapter.setReportTypeSelectedListener(null);
        }

        super.onStop();
    }

    private void setReportTypesAdapter() {

        Action1<ApiResponse<List<String>>> onSuccess = new Action1<ApiResponse<List<String>>>() {
            @Override
            public void call(ApiResponse<List<String>> apiResponse) {
                if (apiResponse.getResult() != null) {
                    List<String> reportList = apiResponse.getResult();
                    if (reportList.size() > 0) {
                        reportTypesListAdapter = new ReportTypesListAdapter(ChooseReportTypeActivity.this, reportList, ChooseReportTypeActivity.this);
                        reportTypesRecyclerView.setAdapter(reportTypesListAdapter);
                    } else {
                        Toast.makeText(ChooseReportTypeActivity.this.getApplicationContext(), R.string.error_loading_report_types, Toast.LENGTH_SHORT).show();
                        ChooseReportTypeActivity.this.finish();
                    }
                }
            }
        };

        Action1<Throwable> onError = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable);
                Toast.makeText(ChooseReportTypeActivity.this.getApplicationContext(), R.string.error_loading_report_types, Toast.LENGTH_SHORT).show();
                ChooseReportTypeActivity.this.finish();
            }
        };

        ApiRequest<GetProblemTypesParams> request = new ApiRequest<>(ApiMethod.GET_PROBLEM_TYPES, null);

        legacyApiService.getProblemTypes(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        onError
                );
    }

    @Override
    public void onReportTypeSelected(String reportType) {
        Intent intent = new Intent();

        intent.putExtra(EXTRA_REPORT_TYPE, reportType);

        setResult(RESULT_OK, intent);

        finish();
    }
}
