package com.example.websockets.services;

import com.example.websockets.models.NewResult;
import com.example.websockets.models.OperationType;
import com.example.websockets.models.UserData;
import com.example.websockets.services.interfaces.INotificationService;
import com.example.websockets.services.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService implements INotificationService {

    private final IUserService userService;
    private final Random random = new Random();
    private final ObjectMapper mapper;
    private ScheduledExecutorService service = new ScheduledThreadPoolExecutor(2);

    public NotificationService(IUserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
        service.scheduleAtFixedRate(() -> {
            userService.getAuthUserData()
                    .entrySet()
                    .stream()
                    .filter(x -> x.getValue().isSubscribeNotification())
                    .forEach(x -> {
                        var operation = generateNotification(x.getValue());
                        x.getValue().addToHistory(operation);
                        var newResult = new NewResult();
                        newResult.setOperation(OperationType.new_result.name());
                        newResult.setData(operation);
                        try {
                            newResult.setJson(mapper.writeValueAsString(newResult));
                            x.getKey().sendMessage(new TextMessage(newResult.toJson()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }, 10, 10, TimeUnit.SECONDS);

        service.scheduleAtFixedRate(()->{

        },5,5,TimeUnit.MINUTES);
    }

    @Override
    public void startNotify(WebSocketSession session) {
        if (userService.getAuthUserData().containsKey(session)) {
            userService.getAuthUserData().get(session).setSubscribeNotification(true);
        }
    }

    @Override
    public void stopNotify(WebSocketSession session) {
        if (userService.getAuthUserData().containsKey(session)) {
            userService.getAuthUserData().get(session).setSubscribeNotification(false);
        }
    }

    public List<List<Integer>> generateNotification(UserData userData) {
        List<List<Integer>> resultList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                int idx = random.nextInt(0, 100);
                int value = userData.getAvailableOperation().get(idx);
                list.add(value);

            }
            resultList.add(list);
        }
        return resultList;
    }


}
