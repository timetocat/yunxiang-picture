package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.common.PageRequest;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.UserRoleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        super.validate();
        if (CharSequenceUtil.isBlank(userRole)) return;
        UserRoleEnum userRoleEnum = BaseValueEnum.getEnumByValue(UserRoleEnum.class, userRole);
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.PARAMS_ERROR);
    }

}
