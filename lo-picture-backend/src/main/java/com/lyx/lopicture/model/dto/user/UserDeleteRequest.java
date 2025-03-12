package com.lyx.lopicture.model.dto.user;

import com.lyx.lopicture.common.DeleteRequest;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

import static com.lyx.lopicture.exception.ErrorCode.PARAMS_ERROR;

public class UserDeleteRequest extends DeleteRequest implements Validator, Serializable {
    @Serial
    private static final long serialVersionUID = -7185823634856539176L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null || id <= 0, PARAMS_ERROR);
    }
}
