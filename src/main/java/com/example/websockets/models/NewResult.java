package com.example.websockets.models;

import com.example.websockets.models.interfaces.IResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class NewResult implements IResult {
    @JsonProperty("operation")
    private String operation;
    @JsonProperty("data")
    private List<List<Integer>> data;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String json;

    @Override
    public String toJson() {
        return json;
    }
}
