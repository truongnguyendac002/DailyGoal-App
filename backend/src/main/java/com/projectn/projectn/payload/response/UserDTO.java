package com.projectn.projectn.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String roleName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("profileImg")
    private String profile_img;

    @JsonProperty("status")
    private String status;

    @JsonProperty("wallet")
    private int wallet;
}
