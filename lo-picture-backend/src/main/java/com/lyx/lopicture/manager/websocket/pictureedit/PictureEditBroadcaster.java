package com.lyx.lopicture.manager.websocket.pictureedit;

import cn.hutool.core.collection.CollUtil;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditResponseMessage;
import com.lyx.lopicture.utils.ObjectMapperUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PictureEditBroadcaster {

    // 保存所有连接的会话，key: pictureId, value: 用户会话集合
    private final Map<Long, Set<WebSocketSession>> pictureSessions = new ConcurrentHashMap<>();

    public void addSession(Long pictureId, WebSocketSession session) {
        pictureSessions.putIfAbsent(pictureId, ConcurrentHashMap.newKeySet());
        pictureSessions.get(pictureId).add(session);
    }

    public void removeSession(Long pictureId, WebSocketSession session) {
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if (sessionSet != null) {
            sessionSet.remove(session);
            if (sessionSet.isEmpty()) {
                pictureSessions.remove(pictureId);
            }
        }
    }

    /**
     * 广播消息给同一张图片的所有用户
     *
     * @param pictureId                  图片 ID
     * @param pictureEditResponseMessage 图片编辑响应消息
     * @param excludeSession             排除的会话
     */
    public void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage,
                                   WebSocketSession excludeSession) throws Exception {
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if (CollUtil.isNotEmpty(sessionSet)) {
            // 序列化为 JSON 字符串
            String message = ObjectMapperUtils.processLongAccuracy(pictureEditResponseMessage);
            TextMessage textMessage = new TextMessage(message);
            for (WebSocketSession session : sessionSet) {
                // 排除掉的 session 不发送
                if (excludeSession != null && excludeSession.equals(session)) {
                    continue;
                }
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        }
    }

    // 全部广播
    public void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage) throws Exception {
        broadcastToPicture(pictureId, pictureEditResponseMessage, null);
    }

}
