package lt.vilnius.tvarkau.API;

import android.support.annotation.Nullable;

public class GetProblemsParams {

    private String description_filter;
    private String type_filter;
    private String address_filter;
    private String reporter_filter;
    private String date_filter;
    private String status_filter;
    private String start;
    private int limit;

    public GetProblemsParams(
        int limit, @Nullable String desctiptionFilter, @Nullable String typeFilter,
        @Nullable String addressFilter, @Nullable String reporterFilter, @Nullable String dateFilter, @Nullable String
        statusFilter, @Nullable String start) {
        this.limit = limit;
        this.description_filter = desctiptionFilter;
        this.type_filter = typeFilter;
        this.address_filter = addressFilter;
        this.reporter_filter = reporterFilter;
        this.date_filter = dateFilter;
        this.status_filter = statusFilter;
        this.start = start;
    }
}


