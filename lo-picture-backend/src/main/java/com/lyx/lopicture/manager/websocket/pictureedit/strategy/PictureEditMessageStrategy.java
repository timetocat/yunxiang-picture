package com.lyx.lopicture.manager.websocket.pictureedit.strategy;

import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import com.lyx.lopicture.model.entity.User;
import org.springframework.web.socket.WebSocketSession;

/**
 * 图片编辑消息处理策略
 */
public interface PictureEditMessageStrategy {

    /**
     * 处理图片编辑请求消息
     *
     * @param requestMessage 图片编辑请求消息
     * @param session        WebSocket会话
     * @param user           用户信息
     * @param pictureId      图片ID
     * @throws Exception 处理异常
     */
    void handle(PictureEditRequestMessage requestMessage, WebSocketSession session,
                User user, Long pictureId) throws Exception;

}

