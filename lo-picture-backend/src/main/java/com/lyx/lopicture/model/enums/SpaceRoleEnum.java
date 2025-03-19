package com.lyx.lopicture.model.enums;

import com.lyx.lopicture.common.BaseValueEnum;
import lombok.Getter;

@Getter
public enum SpaceRoleEnum implements BaseValueEnum<String> {

    VIEWER("浏览者", "viewer"),
    EDITOR("编辑者", "editor"),
    ADMIN("管理员", "admin");

    public static final Class<String> RETURN_TYPE = String.class;

    private final String text;
    private final String value;

    SpaceRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
