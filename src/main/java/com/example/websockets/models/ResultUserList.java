package com.example.websockets.models;

import com.example.websockets.models.interfaces.IResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultUserList implements IResult {
    @JsonProperty("operation")
    private String operation;
    @JsonProperty("data")
    private List<User> data;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String json;

    public ResultUserList() {
    }

    public ResultUserList(List<User> users, OperationType operation) {
        this.data = users;
        this.operation = operation.name();
    }

    @Override
    public String toJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
