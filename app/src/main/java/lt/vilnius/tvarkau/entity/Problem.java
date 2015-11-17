package lt.vilnius.tvarkau.entity;

import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;

import java.util.Date;

import lt.vilnius.tvarkau.R;

/**
 * @author Vilius Kraujutis
 * @since 2015-11-17 03:23.
 */
public class Problem {
    public static final int STATUS_IN_PROGRESS = 0;
    public static final int STATUS_DONE = 1;

    public int id;
    public String title;
    public String description;
    @StatusCode public int statusCode;
    public String statusDescription;
    public Date updatedAt;

    @ColorRes
    public int getColor() {
        switch (statusCode) {
            case STATUS_IN_PROGRESS:
                return R.color.problem_status_in_progress;
            case STATUS_DONE:
            default:
                return R.color.problem_status_in_done;
        }
    }

    @IntDef({STATUS_IN_PROGRESS, STATUS_DONE})
    public @interface StatusCode {
    }
}
