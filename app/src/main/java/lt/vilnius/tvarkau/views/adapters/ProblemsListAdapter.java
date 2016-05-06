package lt.vilnius.tvarkau.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;

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
        holder.titleView.setText(item.title);
        holder.descriptionView.setText(item.description);
        item.applyReportStatusLabel(holder.statusView);
        holder.timeView.setText(DateUtils.getRelativeTimeSpanString(item.updatedAt.getTime()));

        String thumbUrl = item.getThumbUrl();

        if (thumbUrl == null) {
            holder.thumbView.setVisibility(View.GONE);
        } else {
            holder.thumbView.setVisibility(View.VISIBLE);
            Glide.with(activity).load(thumbUrl).into(holder.thumbView);
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ProblemDetailActivity.getStartActivityIntent(activity, holder.item.id);
                Drawable drawable = holder.thumbView.getDrawable();

                Bundle bundle;
                if(holder.thumbView.getVisibility() == View.VISIBLE) {
                    Bitmap bitmap;

                    if (drawable instanceof BitmapDrawable) {
                        bitmap = ((BitmapDrawable) drawable).getBitmap();
                    } else {
                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.draw(canvas);
                    }

                    bundle = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(holder.thumbView, bitmap, 0, 0).toBundle();
                } else {
                    bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0,
                            view.getWidth(), view.getHeight()).toBundle();
                }


                ActivityCompat.startActivity(activity, intent, bundle);
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
