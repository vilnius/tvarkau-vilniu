package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.fragments.ProblemDetailFragment;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;

/**
 * Created by Karolis Vycius on 2016-01-13.
 */
public class ProblemsListAdapter
        extends RecyclerView.Adapter<ProblemsListAdapter.ViewHolder> {

    private Context context;
    private final List<Problem> mValues;

    public ProblemsListAdapter(Context context, List<Problem> items) {
        this.context = context;
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
        holder.titleView.setText(item.title);
        holder.descriptionView.setText(item.description);
        holder.statusView.setText(item.statusDescription);
        holder.statusView.setBackgroundColor(context.getResources().getColor(item.getColor()));
        holder.timeView.setText(DateUtils.getRelativeTimeSpanString(item.updatedAt.getTime()));

        String thumbUrl = item.getThumbUrl();

        if (thumbUrl == null) {
            holder.thumbView.setImageResource(R.drawable.ic_placeholder_list_of_reports);
        } else {
            Picasso.with(context).load(thumbUrl).into(holder.thumbView);
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = ProblemDetailActivity.getStartActivityIntent(context, holder.item.id);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.problem_list_content)
        public View content;
        @Bind(R.id.problem_list_content_title)
        public TextView titleView;
        @Bind(R.id.problem_list_content_description)
        public TextView descriptionView;
        @Bind(R.id.problem_list_content_status)
        public TextView statusView;
        @Bind(R.id.problem_list_content_time)
        public TextView timeView;
        @Bind(R.id.problem_list_content_thumb)
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
