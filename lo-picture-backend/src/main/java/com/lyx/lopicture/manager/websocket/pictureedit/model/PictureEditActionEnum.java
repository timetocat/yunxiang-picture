package com.lyx.lopicture.manager.websocket.pictureedit.model;

import com.lyx.lopicture.common.BaseValueEnum;
import lombok.Getter;

/**
 * 图片编辑动作枚举
 */
@Getter
public enum PictureEditActionEnum implements BaseValueEnum<String> {

    ZOOM_IN("放大操作", "ZOOM_IN"),
    ZOOM_OUT("缩小操作", "ZOOM_OUT"),
    ROTATE_LEFT("左旋操作", "ROTATE_LEFT"),
    ROTATE_RIGHT("右旋操作", "ROTATE_RIGHT");

    public static final Class<String> RETURN_TYPE = String.class;

    private final String text;
    private final String value;

    PictureEditActionEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
