package com.lyx.lopicture.model.enums;

import com.lyx.lopicture.common.BaseValueEnum;
import lombok.Getter;

@Getter
public enum SpaceTypeEnum implements BaseValueEnum<Integer> {

    PRIVATE("私有空间", 0),
    TEAM("团队空间", 1);

    public static final Class<Integer> RETURN_TYPE = Integer.class;

    private final String text;
    private final int value;

    SpaceTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

}
