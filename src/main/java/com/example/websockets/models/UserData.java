package com.example.websockets.models;

import lombok.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserData {
    private User user;
    private Date nextNotify;
    private List<Integer> availableOperation;
    private boolean isSubscribeNotification;
    private final ConcurrentLinkedQueue<OperationHistory> operationHistory = new ConcurrentLinkedQueue<>();
    public UserData(User user, List<Integer> availableOperation, boolean isSubscribeNotification) {
        this.user = user;
        this.availableOperation = availableOperation;
        this.isSubscribeNotification = isSubscribeNotification;
    }

    public void addToHistory(List<List<Integer>> list, Date date) {
        List<Integer> operationList = list.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        operationHistory.offer(new OperationHistory(date,operationList));
    }



}
