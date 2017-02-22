package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.extensions.EntityKt;
import lt.vilnius.tvarkau.utils.FormatUtils;

/**
 * Solution with image loading is based on https://github.com/bumptech/glide/issues/290
 */
public class MapsInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Map<Marker, Bitmap> images = new HashMap<>();
    private final Map<Marker, Target<Bitmap>> targets = new HashMap<>();

    protected View view;
    protected Context context;

    @BindView(R.id.problem_map_info_window_content_title)
    protected TextView titleView;
    @BindView(R.id.problem_map_info_window_content_description)
    protected TextView descriptionView;
    @BindView(R.id.problem_map_info_window_content_status)
    protected TextView statusView;
    @BindView(R.id.problem_map_info_window_content_time)
    protected TextView timeView;
    @BindView(R.id.problem_map_info_window_content_thumb)
    protected ImageView thumbView;

    private Marker currentMarker;
    private int thumbWidth;

    public MapsInfoWindowAdapter(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.problem_map_info_window, null);
        thumbWidth = context.getResources().getDimensionPixelSize(R.dimen.problem_map_info_thumb_width);
        ButterKnife.bind(this, view);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Problem problem = (Problem) marker.getTag();

        descriptionView.setText(problem.getDescription());

        titleView.setText(problem.getType());

        timeView.setText(FormatUtils.formatLocalDateTime(problem.getEntryDate()));

        EntityKt.applyReportStatusLabel(problem, statusView);

        if (problem.getPhotos() != null) {
            thumbView.setVisibility(View.VISIBLE);
            thumbView.setImageBitmap(null); //clear old image
            Bitmap image = images.get(marker);
            if (image == null) {
                Glide.with(context)
                        .load(problem.getPhotos().get(0))
                        .asBitmap()
                        .placeholder(R.drawable.ic_placeholder_list_of_reports)
                        .into(getTarget(marker));
            } else {
                thumbView.setImageBitmap(image);
            }
        } else {
            thumbView.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void showInfoWindow(Marker marker) {
        if (currentMarker != null) {
            clearMarkerTarget(currentMarker);
        }

        currentMarker = marker;
        marker.showInfoWindow();
    }

    /**
     * @return true if window was shown and was dismissed
     */
    public boolean dismissInfoWindow() {
        if (currentMarker != null && currentMarker.isInfoWindowShown()) {
            currentMarker.hideInfoWindow();
            return true;
        }

        return false;
    }

    public void clearMarkerImages() {
        images.clear();
    }

    private void clearMarkerTarget(Marker marker) {
        Target<Bitmap> target = targets.get(marker);
        if (target != null) {
            Glide.clear(target);
        }
    }

    private Target<Bitmap> getTarget(Marker marker) {
        Target<Bitmap> target = targets.get(marker);
        if (target == null) {
            target = new InfoTarget(marker);
            targets.put(marker, target);
        }
        return target;
    }

    private class InfoTarget extends SimpleTarget<Bitmap> {
        Marker marker;

        InfoTarget(Marker marker) {
            super(thumbWidth, Target.SIZE_ORIGINAL);
            this.marker = marker;
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            images.remove(marker);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            images.put(marker, resource);
            // tell the maps API it can try to call getInfoContents again, this time finding the loaded image
            marker.showInfoWindow();
        }
    }
}
