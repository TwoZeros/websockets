package com.example.websockets.services.interfaces;

import com.example.websockets.models.UserData;
import com.example.websockets.models.interfaces.IResult;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

public interface IUserService {
    IResult addUser(TextMessage data);
    IResult changeUserStatus(TextMessage data);
    IResult editUserPassword(TextMessage data);
    IResult getUserList();
    IResult authUser(TextMessage data, WebSocketSession session);
    void logout(WebSocketSession session);
    ConcurrentHashMap<WebSocketSession, UserData> getAuthUserData();
}
