package com.example.websockets;

import com.example.websockets.models.OperationType;
import com.example.websockets.models.interfaces.IResult;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


public interface IRouter {
    public void rout(OperationType operation, TextMessage data, WebSocketSession session) throws IOException;
}
