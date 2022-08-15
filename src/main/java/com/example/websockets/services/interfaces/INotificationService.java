package com.example.websockets.services.interfaces;

import org.springframework.web.socket.WebSocketSession;

public interface INotificationService {
    void startNotify(WebSocketSession session);
    void stopNotify(WebSocketSession session);
}
