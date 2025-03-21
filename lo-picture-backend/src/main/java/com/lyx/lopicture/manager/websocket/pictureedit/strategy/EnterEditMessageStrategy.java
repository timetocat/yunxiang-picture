package com.lyx.lopicture.manager.websocket.pictureedit.strategy;

import com.lyx.lopicture.manager.websocket.pictureedit.PictureEditBroadcaster;
import com.lyx.lopicture.manager.websocket.pictureedit.PictureEditingStatusManager;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditMessageTypeEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditResponseMessage;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.service.UserService;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 进入编辑状态处理策略
 */
@MessageTypeStrategyConfig(type = PictureEditMessageTypeEnum.ENTER_EDIT)
public class EnterEditMessageStrategy implements PictureEditMessageStrategy {

    @Resource
    private UserService userService;

    @Resource
    private PictureEditBroadcaster broadcaster;

    @Resource
    private PictureEditingStatusManager statusManager;


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
        // 没有用户正在编辑图片，才能进入编辑
        if (!statusManager.isBeingEdited(pictureId)) {
            // 设置当前用户为编辑用户
            statusManager.setEditingUser(pictureId, user.getId());
            PictureEditResponseMessage responseMessage = PictureEditResponseMessage.builder()
                    .type(PictureEditMessageTypeEnum.ENTER_EDIT.getValue())
                    .message(String.format("%s开始编辑图片", user.getUserName()))
                    .user(userService.getUserVO(user))
                    .build();
            broadcaster.broadcastToPicture(pictureId, responseMessage);
        }
    }
}
