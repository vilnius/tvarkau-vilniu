package lt.vilnius.tvarkau.entity;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import lt.vilnius.tvarkau.R;

/**
 * @author Vilius Kraujutis
 * @since 2015-11-17 03:23.
 */
public class Problem {
    public static final int STATUS_IN_PROGRESS = 0;
    public static final int STATUS_DONE = 1;

    private int id;
    private String title;
    private String description;

    private String address;
    @StatusCode
    private int statusCode;
    private Date updatedAt;

    private double lat, lng;

    private String thumbUrl;

    public void applyReportStatusLabel(@NonNull TextView reportLabelTextView) {
        switch (statusCode) {
            case STATUS_IN_PROGRESS:
                reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_in_progress);
                reportLabelTextView.setText(R.string.report_status_in_progress);
                break;
            case STATUS_DONE:
            default:
                reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_done);
                reportLabelTextView.setText(R.string.report_status_done);
        }
    }

    public String getAddress() {
        return address;
    }

    @Nullable
    public String getThumbUrl() {
        return thumbUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getRelativeUpdatedAt() {
        return DateUtils.getRelativeTimeSpanString(updatedAt.getTime()).toString();
    }

    @IntDef({STATUS_IN_PROGRESS, STATUS_DONE})
    public @interface StatusCode {
    }
}
