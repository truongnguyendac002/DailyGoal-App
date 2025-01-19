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
public class QuestResponse {
    @JsonProperty("key")
    private UUID id;

    @JsonProperty("target")
    private String target;

    @JsonProperty("forDate")
    private String forDate;

    @JsonProperty("description")
    private String description;

    @JsonProperty("points")
    private int points;

    @JsonProperty("isDone")
    private boolean isDone;
}
