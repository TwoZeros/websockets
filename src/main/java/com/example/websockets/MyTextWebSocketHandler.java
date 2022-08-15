package com.example.websockets;

import com.example.websockets.dto.OperationDto;
import com.example.websockets.models.OperationType;
import com.example.websockets.models.interfaces.IResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.StringReader;

@Component
public class MyTextWebSocketHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyTextWebSocketHandler.class);

    private final ObjectMapper mapper;
    private final IRouter router;

    public  MyTextWebSocketHandler( ObjectMapper mapper, IRouter router ) {
        this.mapper = mapper;
        this.router = router;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        router.rout(OperationType.logout, new TextMessage("logout"), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        OperationDto operation = mapper.readValue(new StringReader(message.getPayload()), OperationDto.class);
        router.rout(operation.getOperation(), message, session);

    }
}
