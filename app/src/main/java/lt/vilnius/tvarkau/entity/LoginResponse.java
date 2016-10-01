package lt.vilnius.tvarkau.entity;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("session_id")
    public String sessionId;

    public String email;

    public String getSessionId() {
        return  sessionId;
    }

    public String getEmail() { return email; }
}
