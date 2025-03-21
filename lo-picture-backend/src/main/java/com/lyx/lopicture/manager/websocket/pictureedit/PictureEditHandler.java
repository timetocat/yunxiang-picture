package com.lyx.lopicture.manager.websocket.pictureedit;

import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditMessageTypeEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditResponseMessage;
import com.lyx.lopicture.manager.websocket.pictureedit.strategy.PictureEditMessageStrategy;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.service.UserService;
import com.lyx.lopicture.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 图片编辑 WebSocket 处理器
 */
@Component
@Slf4j
public class PictureEditHandler extends TextWebSocketHandler {

    @Resource
    private UserService userService;

    @Resource
    private PictureEditBroadcaster broadcaster;

    @Resource
    private PictureEditMessageStrategyExecutor pictureEditMessageStrategyExecutor;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // 保存会话到集合中
        User user = (User) session.getAttributes().get("user");
        Long pictureId = (Long) session.getAttributes().get("pictureId");
        broadcaster.addSession(pictureId, session);
        // 构造响应，发送加入编辑的消息通知
        PictureEditResponseMessage pictureEditResponseMessage = PictureEditResponseMessage.builder()
                .type(PictureEditMessageTypeEnum.INFO.getValue())
                .message(String.format("用户 %s 加入编辑", user.getUserName()))
                .user(userService.getUserVO(user))
                .build();
        // 广播给所有用户
        broadcaster.broadcastToPicture(pictureId, pictureEditResponseMessage);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        // 将消息解析为 PictureEditMessage
        PictureEditRequestMessage pictureEditRequestMessage = ObjectMapperUtils.getObjectMapper()
                .readValue(message.getPayload(), PictureEditRequestMessage.class);
        String type = pictureEditRequestMessage.type();
        // 从 session 中获取公共属性
        Map<String, Object> attributes = session.getAttributes();
        User user = (User) attributes.get("user");
        Long pictureId = (Long) attributes.get("pictureId");
        // 调用消息处理执行器
        pictureEditMessageStrategyExecutor.handleMessage(type, pictureEditRequestMessage,
                session, user, pictureId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        Map<String, Object> attributes = session.getAttributes();
        User user = (User) attributes.get("user");
        Long pictureId = (Long) attributes.get("pictureId");
        // 获取并执行退出策略
        PictureEditMessageStrategy pictureEditMessageStrategy = pictureEditMessageStrategyExecutor
                .getPictureEditMessageStrategyByType(PictureEditMessageTypeEnum.EXIT_EDIT.getValue());
        pictureEditMessageStrategy.handle(null, session, user, pictureId);
        // 删除会话
        broadcaster.removeSession(pictureId, session);
        PictureEditResponseMessage responseMessage = PictureEditResponseMessage.builder()
                .type(PictureEditMessageTypeEnum.INFO.getValue())
                .message(String.format("用户 %s 退出编辑", user.getUserName()))
                .user(userService.getUserVO(user))
                .build();
        broadcaster.broadcastToPicture(pictureId, responseMessage);
    }

}
