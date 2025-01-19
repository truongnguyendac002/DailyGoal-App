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
public class DiceRollResponse {
    @JsonProperty("key")
    private UUID id;

    @JsonProperty("questId")
    private UUID questId;

    @JsonProperty("points")
    private int points;

    @JsonProperty("rollAt")
    private String rollAt;


}
