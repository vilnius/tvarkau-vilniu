package lt.vilnius.tvarkau.entity;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("session_id")
    public String sessionId;

    public String getSessionId() {
        return  sessionId;
    }
}
