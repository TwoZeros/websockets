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
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService implements INotificationService {
    private final static int NOTIFY_DELAY = 10;
    private final static int TIME_STORE_HISTORY = 5;
    private final static int COUNT_GROUP_OPERATION = 4;
    private final static int COUNT_ELEM_IN_GROUP = 5;
    private final IUserService userService;
    private final Random random = new Random();
    private final ObjectMapper mapper;
    private final Calendar calendar = Calendar.getInstance();
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2);

    public NotificationService(IUserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        startNotifyThread();
        startGarbageCollectorThread();
    }

    @Override
    public void startNotify(WebSocketSession session) {
        if (userService.getAuthUserData().containsKey(session)) {
            UserData userData = userService.getAuthUserData().get(session);
            userData.setSubscribeNotification(true);
            userData.setNextNotify(getNextTimeNotify());
        }
    }

    @Override
    public void stopNotify(WebSocketSession session) {
        if (userService.getAuthUserData().containsKey(session)) {
            userService.getAuthUserData().get(session).setSubscribeNotification(false);
        }
    }

    private Date getTime() {
        calendar.setTimeInMillis(new Date().getTime());
        return calendar.getTime();
    }

    private Date getNextTimeNotify() {
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.SECOND, NOTIFY_DELAY);
        return calendar.getTime();
    }

    public List<List<Integer>> generateNotification(UserData userData) {
        List<List<Integer>> resultList = new ArrayList<>();
        for (int i = 0; i < COUNT_GROUP_OPERATION; i++) {
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < COUNT_ELEM_IN_GROUP; j++) {
                int idx = random.nextInt(0, 100);
                int value = userData.getAvailableOperation().get(idx);
                list.add(value);
            }
            resultList.add(list);
        }
        return resultList;
    }

    private void startNotifyThread() {
        executorService.scheduleAtFixedRate(() -> {
            userService.getAuthUserData()
                    .entrySet()
                    .stream()
                    .filter(x -> x.getValue().isSubscribeNotification())
                    .filter(x -> x.getValue().getNextNotify().before(getTime())
                            || x.getValue().getNextNotify().equals(getTime())
                    )
                    .forEach(x -> {
                        var operation = generateNotification(x.getValue());
                        UserData userData = x.getValue();
                        //Обновляем пользовательские данные
                        userData.addToHistory(operation, getTime());
                        userData.setNextNotify(getNextTimeNotify());
                        //Формируем результат
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
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void startGarbageCollectorThread() {
        executorService.scheduleAtFixedRate(() -> {
            userService.getAuthUserData()
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .forEach(userData -> {
                        var time = getTime();
                        var operationHistory = userData.getOperationHistory();
                        while (operationHistory.peek() != null && time.after(
                                getTimeToDel(
                                        operationHistory.peek().getDateSend()))) {
                        }
                    });
        }, 1, 1, TimeUnit.SECONDS);
    }

    private Date getTimeToDel(Date date) {
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MINUTE, TIME_STORE_HISTORY);
        return calendar.getTime();
    }
}
