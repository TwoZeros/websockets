package com.example.websockets.services;

import com.example.websockets.models.OperationType;
import com.example.websockets.models.interfaces.IResult;
import com.example.websockets.services.interfaces.INotificationService;
import com.example.websockets.services.interfaces.IRouter;
import com.example.websockets.services.interfaces.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public class Router implements IRouter {
    private final IUserService userService;
    private final INotificationService notificationService;

    public Router(IUserService userService, INotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public void rout(OperationType operationType, TextMessage message, WebSocketSession session) throws IOException {

        switch (operationType) {
            case get_user_list -> {
                IResult result = userService.getUserList();
                session.sendMessage(new TextMessage(result.toJson()));
            }
            case add_user -> {
                IResult result = userService.addUser(message);
                session.sendMessage(new TextMessage(result.toJson()));
            }
            case edit_user_password -> {
                IResult result = userService.editUserPassword(message);
                session.sendMessage(new TextMessage(result.toJson()));
            }
            case change_user_status -> {
                IResult result = userService.changeUserStatus(message);
                session.sendMessage(new TextMessage(result.toJson()));
            }
            case auth -> {
                IResult result = userService.authUser(message, session);
                session.sendMessage(new TextMessage(result.toJson()));
            }
            case start_job -> {
                notificationService.startNotify(session);
            }
            case stop_job -> {
                notificationService.stopNotify(session);
            }
            case logout -> {
                userService.logout(session);
            }
        }


    }
}
