package com.lyx.lopicture.manager.websocket.pictureedit.model;

import com.lyx.lopicture.model.vo.UserVO;
import lombok.Builder;

/**
 * 图片编辑响应消息
 *
 * @param type       消息类型，例如 "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
 * @param message    信息
 * @param editAction 执行的编辑动作
 * @param user       用户信息
 */
@Builder
public record PictureEditResponseMessage(
        String type,
        String message,
        String editAction,
        UserVO user
) {
}
