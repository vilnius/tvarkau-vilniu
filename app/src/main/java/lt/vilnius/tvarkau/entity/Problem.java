package lt.vilnius.tvarkau.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

import lt.vilnius.tvarkau.R;

/**
 * @author Vilius Kraujutis
 * @since 2015-11-17 03:23.
 */
@Parcel
public class Problem {
    public static final String STATUS_DONE = "Atlikta";
    public static final String STATUS_RESOLVED = "Išnagrinėta";
    public static final String STATUS_TRANSFERRED = "Perduota";
    public static final String STATUS_REGISTERED = "Registruota";
    public static final String STATUS_POSTPONED = "Atidėta";
    public static final String STATUS_IN_PROGRESS = "Vykdoma";

    @SerializedName("docNo")
    public String id;

    // entry_date and report_date brings back the
    // same result just from different API methods
    @SerializedName("entry_date")
    public String entryDate;

    @SerializedName("report_date")
    public String reportDate;

    @SerializedName("time_ago")
    public String timeAgo;

    // type_name and type brings back the
    // same result just from different API methods
    @SerializedName("type_name")
    public String typeName;

    public String type;

    public String description;
    public String address;
    public String status;
    public String answer;

    @SerializedName("x")
    public double lng;

    @SerializedName("y")
    public double lat;

    @SerializedName("photo")
    public ArrayList<String> photos;

    //  private Date updatedAt;

    @SerializedName("thumbnail")
    public String thumbUrl;

    public void applyReportStatusLabel(String status, @NonNull TextView reportLabelTextView) {
        reportLabelTextView.setText(status);
        if (status.equalsIgnoreCase(STATUS_DONE) || (status.equalsIgnoreCase(STATUS_RESOLVED))) {
            reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_done);
        } else if (status.equalsIgnoreCase(STATUS_IN_PROGRESS)) {
            reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_in_progress);
        } else if (status.equalsIgnoreCase(STATUS_POSTPONED)) {
            reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_postponed);
        } else if (status.equalsIgnoreCase(STATUS_REGISTERED)) {
            reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_registered);
        } else if (status.equalsIgnoreCase(STATUS_TRANSFERRED)) {
            reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_transferred);
        }
    }

    public String getAddress() {
        return address;
    }

    @Nullable
    public String getThumbUrl() { return thumbUrl; }

    public String getStatus() {
        return status;
    }

    public String getType() { return type; }
    public String getTypeName() { return typeName; }

    public String getAnswer() { return answer; }

    public String getEntryDate() { return entryDate; }
    public String getReportDate() { return reportDate; }

    public String getTimeAgo() { return timeAgo; }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public ArrayList<String> getPhotos() { return photos; }

    //    public Date getUpdatedAt() {
    //        return updatedAt;
    //    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //    public void setUpdatedAt(Date updatedAt) {
    //        this.updatedAt = updatedAt;
    //    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setThumbUrl(String thumbUrl) {this.thumbUrl = thumbUrl; }

    //    public String getRelativeUpdatedAt() {
    //        return DateUtils.getRelativeTimeSpanString(updatedAt.getTime()).toString();
    //    }
}
