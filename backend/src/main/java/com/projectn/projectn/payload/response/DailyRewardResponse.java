package com.projectn.projectn.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRewardResponse {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String imageUrl;

    @JsonProperty("description")
    private String description;

    @JsonProperty("minPoint")
    private int minPoint;

    @JsonProperty("status")
    private String status;
}
