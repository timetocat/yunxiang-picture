package com.lyx.lopicture.manager.websocket.pictureedit.strategy;

import cn.hutool.core.util.ObjUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.PictureEditBroadcaster;
import com.lyx.lopicture.manager.websocket.pictureedit.PictureEditingStatusManager;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditActionEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditMessageTypeEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditResponseMessage;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.service.UserService;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 执行编辑操作处理策略
 */
@MessageTypeStrategyConfig(type = PictureEditMessageTypeEnum.EDIT_ACTION)
public class EditActionMessageStrategy implements PictureEditMessageStrategy {

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
        String editAction = requestMessage.editAction();
        PictureEditActionEnum actionEnum = BaseValueEnum
                .getEnumByValue(PictureEditActionEnum.class, editAction);
        if (actionEnum == null) return;
        // 确认是当前编辑者
        if (ObjUtil.isNotNull(editingUserId) && editingUserId.equals(user.getId())) {
            PictureEditResponseMessage responseMessage = PictureEditResponseMessage.builder()
                    .type(PictureEditMessageTypeEnum.EDIT_ACTION.getValue())
                    .message(String.format("%s执行了%s", user.getUserName(), actionEnum.getText()))
                    .user(userService.getUserVO(user))
                    .build();
            // 广播给除了当前客户端之外的其他用户，否则会造成重复编辑
            broadcaster.broadcastToPicture(pictureId, responseMessage, session);
        }
    }
}
