package lt.vilnius.tvarkau.entity;

import android.support.annotation.IntDef;

import java.util.Date;
import java.util.List;

/**
 * @author Vilius Kraujutis
 * @since 2015-11-17 03:23.
 */
public class Problem {
    public static final int STATUS_IN_PROGRESS = 0;
    public static final int STATUS_DONE = 1;

    public String id;
    public String title;
    public String description;
    @StatusCode public int statusCode;
    public String statusDescription;
    public Date updatedAt;


    @IntDef({STATUS_IN_PROGRESS, STATUS_DONE})
    public @interface StatusCode {
    }
}
