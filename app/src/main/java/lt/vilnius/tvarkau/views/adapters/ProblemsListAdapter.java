package lt.vilnius.tvarkau.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.utils.FormatUtils;

public class ProblemsListAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROGRESS = 0;

    private Activity activity;
    private final List<Problem> values;
    private boolean showLoader;

    public ProblemsListAdapter(Activity activity, List<Problem> items) {
        this.activity = activity;
        values = items;
        showLoader = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.problem_list_content, parent, false);
            viewHolder = new DataViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_indicator, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DataViewHolder) {
            Problem item = values.get(position);

            DataViewHolder dataViewHolder = (DataViewHolder) holder;
            dataViewHolder.item = item;

            dataViewHolder.descriptionView.setText(item.getDescription());

            item.applyReportStatusLabel(item.getStatus(), dataViewHolder.statusView);

            dataViewHolder.titleView.setText(item.getType());

            dataViewHolder.timeView.setText(FormatUtils.formatLocalDateTime(item.getEntryDate()));

            if (item.getPhotos() != null) {
                dataViewHolder.thumbView.setVisibility(View.VISIBLE);
                Glide.with(activity)
                    .load(item.getPhotos()[0])
                    .into(dataViewHolder.thumbView);
            } else {
                dataViewHolder.thumbView.setVisibility(View.GONE);
            }

            dataViewHolder.content.setOnClickListener(view -> {
                Intent intent = ProblemDetailActivity.getStartActivityIntent(activity, dataViewHolder.item.getId());

                Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0,
                            view.getWidth(), view.getHeight()).toBundle();

                ActivityCompat.startActivity(activity, intent, bundle);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < values.size()
            ? VIEW_ITEM
            : VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return showLoader
            ? values.size() + 1
            : values.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.problem_list_content)
        public View content;
        @BindView(R.id.problem_list_content_title)
        public TextView titleView;
        @BindView(R.id.problem_list_content_description)
        public TextView descriptionView;
        @BindView(R.id.problem_list_content_status)
        public TextView statusView;
        @BindView(R.id.problem_list_content_time)
        public TextView timeView;
        @BindView(R.id.problem_list_content_thumb)
        protected ImageView thumbView;

        public Problem item;

        public DataViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + descriptionView.getText() + "'";
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressViewHolder(View view) {
            super(view);
        }
    }

    public void hideLoader() {
        showLoader = false;
    }
}
