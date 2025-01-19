package com.projectn.projectn.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    @JsonProperty("Email")
    private String email;
    @JsonProperty("Password")
    private String password;
}
