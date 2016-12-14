package lt.vilnius.tvarkau.backend;

import android.support.annotation.Nullable;

public class GetNewProblemParams {

    private String sessionId;
    private String description;
    private String type;
    private String address;
    private double x;
    private double y;
    private String[] photo;
    private String email;
    private String phone;
    private String messageDescription;
    private String nameOfReporter;
    private String dateOfBirth;

    public GetNewProblemParams(String sessionId, String description, String type, String address, double latitude,
        double longitude, @Nullable String[] photo, @Nullable String email, @Nullable String phone,
        @Nullable String messageDescription, @Nullable String nameOfReporter, @Nullable String dateOfBirth) {
        this.sessionId = sessionId;
        this.description = description;
        this.type = type;
        this.address = address;
        this.x = latitude;
        this.y = longitude;
        this.photo = photo;
        this.email = email;
        this.phone = phone;
        this.messageDescription = messageDescription;
        this.nameOfReporter = nameOfReporter;
        this.dateOfBirth = dateOfBirth;

    }

    public String[] getPhoto() {
        return photo;
    }

    public static class Builder {
        private String sessionId;
        private String description;
        private String type;
        private String address;
        private double latitude;
        private double longitude;
        private String[] photo;
        private String email;
        private String phone;
        private String messageDescription;
        private String nameOfReporter;
        private String dateOfBirth;

        public Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setPhoto(String[] photo) {
            this.photo = photo;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setMessageDescription(String messageDescription) {
            this.messageDescription = messageDescription;
            return this;
        }

        public Builder setNameOfReporter(String nameOfReporter){
            this.nameOfReporter = nameOfReporter;
            return this;
        }

        public Builder setDateOfBirth(String dateOfBirth){
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public GetNewProblemParams create() {
            return new GetNewProblemParams(sessionId, description, type, address, latitude, longitude, photo, email,
                phone, messageDescription, nameOfReporter, dateOfBirth);
        }
    }
}
