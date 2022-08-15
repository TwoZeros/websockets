package com.example.websockets.services.interfaces;

import com.example.websockets.models.interfaces.IResult;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public interface IUserService {
    IResult addUser(TextMessage data);
    IResult changeUserStatus(TextMessage data);
    IResult editUserPassword(TextMessage data);
    IResult getUserList();
    IResult authUser(TextMessage data, WebSocketSession session);
}
