package com.lyx.lopicture.model.enums;

import com.lyx.lopicture.common.BaseValueEnum;
import lombok.Getter;

@Getter
public enum PictureReviewStatusEnum implements BaseValueEnum<Integer> {

    REVIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);

    final Class<Integer> RETURN_TYPE = Integer.class;

    private final String text;
    private final Integer value;

    PictureReviewStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }
}
