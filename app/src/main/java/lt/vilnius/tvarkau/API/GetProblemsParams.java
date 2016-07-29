package lt.vilnius.tvarkau.API;

import android.support.annotation.Nullable;

public class GetProblemsParams {

    private int start;
    private int limit;
    private String description_filter;
    private String type_filter;
    private String address_filter;
    private String reporter_filter;
    private String date_filter;
    private String status_filter;

    public GetProblemsParams(int start, int limit, @Nullable String descriptionFilter, @Nullable String typeFilter,
        @Nullable String addressFilter, @Nullable String reporterFilter, @Nullable String dateFilter,
        @Nullable String statusFilter) {
        this.start = start;
        this.limit = limit;
        this.description_filter = descriptionFilter;
        this.type_filter = typeFilter;
        this.address_filter = addressFilter;
        this.reporter_filter = reporterFilter;
        this.date_filter = dateFilter;
        this.status_filter = statusFilter;
    }
}


