package com.example.websockets;

import com.example.websockets.models.OperationType;
import com.example.websockets.models.interfaces.IResult;
import com.example.websockets.services.interfaces.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class Router implements IRouter {
    private IUserService userService;
    public Router(IUserService userService) {
        this.userService = userService;
    }
    public IResult rout(OperationType operationType, TextMessage message, WebSocketSession session) {
       return switch (operationType) {
            case get_user_list -> userService.getUserList();
            case add_user -> userService.addUser(message);
            case edit_user_password -> userService.editUserPassword(message);
            case change_user_status -> userService.changeUserStatus(message);
            case auth -> userService.authUser(message, session);
            case new_result ->  userService.changeUserStatus(message);
           default -> throw new IllegalStateException("Unexpected value: " + operationType);
       };

    }
}
