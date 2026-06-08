package com.irallyin.server.core.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleTokenInfoResponse {
    private String iss;
    private String aud;
    private String sub;
    private String email;
    private String name;
    private String picture;
    private String locale;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    private Long exp;
}
