package lt.vilnius.tvarkau.entity;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.threeten.bp.LocalDateTime;

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

    @SerializedName("docNo")
    public String id;

    @SerializedName("problem_id")
    public String idForVilniusAccount;

    // entry_date and report_date bring back the
    // same result just from different API methods
    @SerializedName("entry_date")
    public LocalDateTime entryDateInGetProblemsAPI;
    @SerializedName("report_date")
    public LocalDateTime entryDateInGetReportAPI;

    // type_name and type bring back the
    // same result just from different API methods
    @SerializedName("type_name")
    public String typeInGetProblemsAPI;
    @SerializedName("type")
    public String typeInGetReportAPI;

    public String description;
    public String address;
    public String status;
    public String answer;

    @SerializedName("x")
    public double lng;

    @SerializedName("y")
    public double lat;

    @SerializedName("photo")
    public String[] photosInGetReportAPI;

    @SerializedName("thumbnail")
    public String photosInGetProblemsAPI;

    @SerializedName("complete_date")
    public String answerDate;

    public void applyReportStatusLabel(String status, @NonNull TextView reportLabelTextView) {
        reportLabelTextView.setText(status);
        if (status.equalsIgnoreCase(STATUS_DONE) || (status.equalsIgnoreCase(STATUS_RESOLVED))) {
            reportLabelTextView.setBackgroundResource(R.drawable.label_report_status_done);
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

    public String getStatus() {
        return status;
    }

    public String getType() {
        if (typeInGetProblemsAPI != null) {
            return typeInGetProblemsAPI;
        } else {
            return typeInGetReportAPI;
        }
    }

    public String getAnswer() { return answer; }

    public LocalDateTime getEntryDate() {
        if (entryDateInGetProblemsAPI != null) {
            return entryDateInGetProblemsAPI;
        } else {
            return entryDateInGetReportAPI;
        }
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getIdForVilniusAccount() { return idForVilniusAccount; }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public String getAnswerDate() { return answerDate; }

    public String[] getPhotos() {
        if (photosInGetReportAPI != null) {
            return photosInGetReportAPI;
        } else if (photosInGetProblemsAPI != null) {
            String[] photosArray = new String[1];
            photosArray[0] = photosInGetProblemsAPI;
            return photosArray;
        } else {
            return null;
        }
    }

    public void setId(String id) {
        this.id = id;
    }
}
