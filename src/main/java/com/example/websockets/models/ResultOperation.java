package com.example.websockets.models;

import com.example.websockets.models.interfaces.IResult;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ResultOperation implements IResult {
    private OperationType operation;
    private boolean status;

    @Override
    public String toJson() {
        return String.format("{ \"operation\": \"%s\", \"status\": %b }", operation.name(), status);
    }
}
