package lt.vilnius.tvarkau.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;

/**
 * Created by Karolis Vycius on 2016-01-30.
 */
public class MapsInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    protected HashMap<String, Problem> problemHashMap;
    protected View view;
    protected Context context;

    @Bind(R.id.problem_map_info_window_content_title)
    protected TextView titleView;
    @Bind(R.id.problem_map_info_window_content_description)
    protected TextView descriptionView;
    @Bind(R.id.problem_map_info_window_content_status)
    protected TextView statusView;
    @Bind(R.id.problem_map_info_window_content_time)
    protected TextView timeView;
    @Bind(R.id.problem_map_info_window_content_thumb)
    protected ImageView thumbView;

    public MapsInfoWindowAdapter(Activity activity, HashMap<String, Problem> problemHashMap) {
        this.problemHashMap = problemHashMap;
        context = activity;
        view = activity.getLayoutInflater().inflate(R.layout.problem_map_info_window, null);

        ButterKnife.bind(this, view);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Problem problem = problemHashMap.get(marker.getTitle());

        titleView.setText(problem.getTitle());
        descriptionView.setText(problem.getDescription());
        timeView.setText(problem.getRelativeUpdatedAt());

        statusView.setBackgroundColor(ContextCompat.getColor(context, problem.getColor()));
        statusView.setText(problem.getStatusDescription());

        String thumbUrl = problem.getThumbUrl();

        if (thumbUrl == null) {
            thumbView.setImageResource(R.drawable.ic_placeholder_list_of_reports);
        } else {
            Picasso.with(context).load(thumbUrl).placeholder(R.drawable.ic_placeholder_list_of_reports).into(thumbView);
        }

        return view;
    }
}
