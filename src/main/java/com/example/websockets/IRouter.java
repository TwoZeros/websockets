package com.example.websockets;

import com.example.websockets.models.OperationType;
import com.example.websockets.models.interfaces.IResult;
import org.springframework.web.socket.TextMessage;


public interface IRouter {
    public IResult rout(OperationType operation, TextMessage data);
}
