package com.example.websockets.dto;

import com.example.websockets.models.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationDto {
    private String operation;

    public OperationType getOperation() {
        return OperationType.valueOf(operation);
    }
}
