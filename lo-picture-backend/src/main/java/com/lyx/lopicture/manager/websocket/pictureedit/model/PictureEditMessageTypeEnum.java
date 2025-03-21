package com.lyx.lopicture.manager.websocket.pictureedit.model;

import com.lyx.lopicture.common.BaseValueEnum;
import lombok.Getter;

/**
 * 图片编辑消息类型枚举
 */
@Getter
public enum PictureEditMessageTypeEnum implements BaseValueEnum<String> {

    INFO("发送通知", "INFO"),
    ERROR("发送错误", "ERROR"),
    ENTER_EDIT("进入编辑状态", "ENTER_EDIT"),
    EXIT_EDIT("退出编辑状态", "EXIT_EDIT"),
    EDIT_ACTION("执行编辑操作", "EDIT_ACTION");

    public static final Class<String> RETURN_TYPE = String.class;

    private final String text;
    private final String value;

    PictureEditMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
