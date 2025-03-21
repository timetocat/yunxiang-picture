package com.lyx.lopicture.manager.websocket.pictureedit.strategy;

import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditMessageTypeEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditResponseMessage;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.service.UserService;
import com.lyx.lopicture.utils.ObjectMapperUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 错误消息处理策略
 */
@MessageTypeStrategyConfig(type = PictureEditMessageTypeEnum.ERROR)
public class ErrorMessageStrategy implements PictureEditMessageStrategy {

    @Resource
    private UserService userService;

    /**
     * 处理图片编辑请求消息
     *
     * @param requestMessage 图片编辑请求消息
     * @param session        WebSocket会话
     * @param user           用户信息
     * @param pictureId      图片ID
     * @throws Exception 处理异常
     */
    @Override
    public void handle(PictureEditRequestMessage requestMessage, WebSocketSession session,
                       User user, Long pictureId) throws Exception {
        PictureEditResponseMessage responseMessage = PictureEditResponseMessage.builder()
                .type(PictureEditMessageTypeEnum.ERROR.getValue())
                .message("消息类型错误")
                .user(userService.getUserVO(user))
                .build();
        String message = ObjectMapperUtils.processLongAccuracy(responseMessage);
        session.sendMessage(new TextMessage(message));
    }
}
