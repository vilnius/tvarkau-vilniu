package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.entity.ReportType;
import lt.vilnius.tvarkau.views.adapters.ReportTypesListAdapter;

import static lt.vilnius.tvarkau.views.adapters.ReportTypesListAdapter.ReportTypeSelectedListener;

public class ChooseReportTypeActivity extends AppCompatActivity implements ReportTypeSelectedListener {

    public static final String EXTRA_REPORT_TYPE = "ChooseReportTypeActivity.reportType";

    @Bind(R.id.report_types_recycler_view)
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
        List<ReportType> reportTypes = getReportTypes();

        reportTypesListAdapter = new ReportTypesListAdapter(this, reportTypes);

        reportTypesRecyclerView.setAdapter(reportTypesListAdapter);
    }

    private List<ReportType> getReportTypes() {
        String[] types = getResources().getStringArray(R.array.problem_types);

        ArrayList<ReportType> reportTypes = new ArrayList<>(types.length);

        for (String type : types) {
            reportTypes.add(new ReportType(type));
        }

        return reportTypes;
    }

    @Override
    public void onReportTypeSelected(ReportType reportType) {
        Intent intent = new Intent();

        intent.putExtra(EXTRA_REPORT_TYPE, reportType);

        setResult(RESULT_OK, intent);

        finish();
    }
}
