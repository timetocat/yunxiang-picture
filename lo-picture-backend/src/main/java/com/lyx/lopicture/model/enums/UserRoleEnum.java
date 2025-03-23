package com.lyx.lopicture.model.enums;

import com.lyx.lopicture.common.BaseValueEnum;
import lombok.Getter;

@Getter
public enum UserRoleEnum implements BaseValueEnum<String> {

    USER("用户", "user"),
    ADMIN("管理员", "admin"),
    /*BAN("被封号", "ban")*/
    VIP("会员", "vip");

    final Class<String> RETURN_TYPE = String.class;

    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
