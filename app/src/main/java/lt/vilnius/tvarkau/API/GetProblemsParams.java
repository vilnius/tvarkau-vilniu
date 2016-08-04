package lt.vilnius.tvarkau.api;

import android.support.annotation.Nullable;

public class GetProblemsParams {

    private int start;
    private int limit;
    private String descriptionFilter;
    private String typeFilter;
    private String addressFilter;
    private String reporterFilter;
    private String dateFilter;
    private String statusFilter;

    public GetProblemsParams(int start, int limit, @Nullable String descriptionFilter, @Nullable String typeFilter,
        @Nullable String addressFilter, @Nullable String reporterFilter, @Nullable String dateFilter,
        @Nullable String statusFilter) {
        this.start = start;
        this.limit = limit;
        this.descriptionFilter = descriptionFilter;
        this.typeFilter = typeFilter;
        this.addressFilter = addressFilter;
        this.reporterFilter = reporterFilter;
        this.dateFilter = dateFilter;
        this.statusFilter = statusFilter;
    }

    public static class Builder {
        private int start;
        private int limit;
        private String descriptionFilter;
        private String typeFilter;
        private String addressFilter;
        private String reporterFilter;
        private String dateFilter;
        private String statusFilter;

        public Builder setStart(int start) {
            this.start = start;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder setDescriptionFilter(String descriptionFilter) {
            this.descriptionFilter = descriptionFilter;
            return this;
        }

        public Builder setTypeFilter(String typeFilter) {
            this.typeFilter = typeFilter;
            return this;
        }

        public Builder setAddressFilter(String addressFilter) {
            this.addressFilter = addressFilter;
            return this;
        }

        public Builder setReporterFilter(String reporterFilter) {
            this.reporterFilter = reporterFilter;
            return this;
        }

        public Builder setDateFilter(String dateFilter) {
            this.dateFilter = dateFilter;
            return this;
        }

        public Builder setStatusFilter(String statusFilter) {
            this.statusFilter = statusFilter;
            return this;
        }

        public GetProblemsParams create() {
            return new GetProblemsParams(start, limit, descriptionFilter, typeFilter, addressFilter,
                reporterFilter, dateFilter, statusFilter);
        }
    }
}


