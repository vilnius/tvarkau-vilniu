package lt.vilnius.tvarkau.API;

import android.support.annotation.Nullable;

public class GetNewProblemParams {

    private String session_id;
    private String description;
    private String type;
    private String address;
    private double x;
    private double y;
    private String[] photo;
    private String email;
    private String phone;
    private String message_description;

    public GetNewProblemParams(String session_id, String description, String type, String address, double latitude,
        double longitude, @Nullable String[] photo, @Nullable String email, @Nullable String phone,
        @Nullable String messageDescription) {
        this.session_id = session_id;
        this.description = description;
        this.type = type;
        this.address = address;
        this.x = latitude;
        this.y = longitude;
        this.photo = photo;
        this.email = email;
        this.phone = phone;
        this.message_description = messageDescription;
    }
}
