package com.example.websockets.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationHistory {
    private Date dateSend;
    private List<Integer> operations;

    @Override
    public String toString() {
        return "OperationHistory{" +
                "dateSend=" + dateSend +
                ", operations=" + operations +
                '}';
    }
}
