package com.example.websockets.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserData {
    private User user;
    private List<Integer> availableOperation;
    private boolean isSubscribeNotification;
    private List<Integer> history;
    public UserData(User user, List<Integer> availableOperation, boolean isSubscribeNotification) {
        this.user = user;
        this.availableOperation = availableOperation;
        this.isSubscribeNotification = isSubscribeNotification;
    }

    public void addToHistory(List<List<Integer>> list) {
        List<Integer> flat = list.stream()
                .flatMap(List::stream)
                .toList();
        history.addAll(flat);
    }



}
