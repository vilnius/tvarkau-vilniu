package lt.vilnius.tvarkau.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.ReportType;

public class ReportTypesListAdapter extends RecyclerView.Adapter<ReportTypesListAdapter.ViewHolder> {

    private List<ReportType> reportTypes;
    private ReportTypeSelectedListener listener;

    public ReportTypesListAdapter(ReportTypeSelectedListener listener, List<ReportType> reportTypes) {
        this.reportTypes = reportTypes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_type, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReportType reportType = reportTypes.get(position);

        holder.item = reportType;
        holder.reportTypeName.setText(reportType.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onReportTypeSelected(reportType);
        });
    }

    public void setReportTypeSelectedListener(ReportTypeSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return reportTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ReportType item;

        @Bind(R.id.item_report_type_name)
        public TextView reportTypeName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ReportTypeSelectedListener {

        void onReportTypeSelected(ReportType reportType);

    }
}
