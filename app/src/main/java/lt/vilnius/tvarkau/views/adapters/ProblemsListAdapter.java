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

/**
 * Created by Karolis Vycius on 2016-01-13.
 */
public class ProblemsListAdapter
        extends RecyclerView.Adapter<ProblemsListAdapter.ViewHolder> {

    private Activity activity;
    private final List<Problem> mValues;

    public ProblemsListAdapter(Activity activity, List<Problem> items) {
        this.activity = activity;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.problem_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Problem item = mValues.get(position);

        holder.item = item;

        holder.descriptionView.setText(item.getDescription());
        item.applyReportStatusLabel(item.getStatus(), holder.statusView);

        // Check problem type in both APIs as same data is called differently
        if (item.getTypeName() != null) {
            holder.titleView.setText(item.getTypeName());
        } else {
            holder.titleView.setText(item.getType());
        }

        // Check date in both APIs as same data is called differently
        if (item.getReportDate() != null) {
            holder.timeView.setText(FormatUtils.formatLocalDateTime(item.getReportDate()));
        } else {
            holder.timeView.setText(FormatUtils.formatLocalDateTime(item.getEntryDate()));
        }

        // Check photo thumbnail in both APIs as same data is called differently
        if (item.getThumbUrl() != null) {
            holder.thumbView.setVisibility(View.VISIBLE);
            Glide.with(activity).load(item.getThumbUrl()).into(holder.thumbView);
        } else if (item.getPhotos() != null) {
            String[] photos = item.getPhotos();
            holder.thumbView.setVisibility(View.VISIBLE);
            Glide.with(activity).load(photos[0]).into(holder.thumbView);
        } else {
            holder.thumbView.setVisibility(View.GONE);
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ProblemDetailActivity.getStartActivityIntent(activity, holder.item.getId());

                Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0,
                            view.getWidth(), view.getHeight()).toBundle();

                ActivityCompat.startActivity(activity, intent, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + descriptionView.getText() + "'";
        }
    }
}
