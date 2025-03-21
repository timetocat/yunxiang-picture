package com.lyx.lopicture.manager.websocket.pictureedit.strategy;

import cn.hutool.core.util.ObjUtil;
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
 * 退出编辑图片消息处理策略
 */
@MessageTypeStrategyConfig(type = PictureEditMessageTypeEnum.EXIT_EDIT)
public class ExitEditMessageStrategy implements PictureEditMessageStrategy {

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
        Long editingUserId = statusManager.getEditingUser(pictureId);
        if (ObjUtil.isNotNull(editingUserId) && editingUserId.equals(user.getId())) {
            // 移除当前用户编辑状态
            statusManager.removeEditingUser(pictureId);
            PictureEditResponseMessage responseMessage = PictureEditResponseMessage.builder()
                    .type(PictureEditMessageTypeEnum.EXIT_EDIT.getValue())
                    .message(String.format("%s退出编辑图片", user.getUserName()))
                    .user(userService.getUserVO(user))
                    .build();
            broadcaster.broadcastToPicture(pictureId, responseMessage);
        }
    }
}
