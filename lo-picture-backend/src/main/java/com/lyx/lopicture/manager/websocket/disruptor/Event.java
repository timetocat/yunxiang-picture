package com.lyx.lopicture.manager.websocket.disruptor;

import com.lyx.lopicture.model.entity.User;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class Event {

    /**
     * 当前用户的 session
     */
    private WebSocketSession session;

    /**
     * 当前用户
     */
    private User user;

}
