package com.example.websockets.services;

import com.example.websockets.models.interfaces.IResult;
import org.springframework.web.socket.TextMessage;

public interface IUserService {
    IResult addUser(TextMessage data);
    IResult changeUserStatus(TextMessage data);
    IResult editUserPassword(TextMessage data);
    IResult getUserList();
}
