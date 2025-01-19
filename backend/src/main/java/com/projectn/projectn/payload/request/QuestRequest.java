package com.projectn.projectn.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuestRequest {
    @JsonProperty("target")
    private String target;

    @JsonProperty("description")
    private String description;

    @JsonProperty("forDate")
    private String forDate;

    @JsonProperty("isDone")
    private boolean isDone;

}
