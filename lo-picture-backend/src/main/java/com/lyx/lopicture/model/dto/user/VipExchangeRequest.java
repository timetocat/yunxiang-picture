package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员兑换请求
 *
 * @param vipCode 兑换码
 */
public record VipExchangeRequest(
        String vipCode
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.isBlank(vipCode), ErrorCode.PARAMS_ERROR);
    }
}
