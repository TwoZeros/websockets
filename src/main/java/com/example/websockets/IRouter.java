package com.example.websockets;

import com.example.websockets.models.OperationType;
import com.example.websockets.models.interfaces.IResult;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


public interface IRouter {
    public IResult rout(OperationType operation, TextMessage data, WebSocketSession session);
}
