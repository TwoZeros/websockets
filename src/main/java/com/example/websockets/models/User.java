package com.example.websockets.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty("user_id")
    private Integer id;
    @JsonProperty("login")
    private String login;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty("last_auth")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone="Europe/Moscow")
    private Date lastAuthDate;
    @JsonProperty("status")
    private boolean active;

    public void inversStatus() {
        active = !active;
    }
}
