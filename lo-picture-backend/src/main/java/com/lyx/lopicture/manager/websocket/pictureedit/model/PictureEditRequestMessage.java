package com.lyx.lopicture.manager.websocket.pictureedit.model;

/**
 * 图片编辑请求消息
 *
 * @param type       消息类型，例如 "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
 * @param editAction 执行的编辑动作（放大、缩小、左右旋转等）
 */
public record PictureEditRequestMessage(
        String type,
        String editAction
) {
}
