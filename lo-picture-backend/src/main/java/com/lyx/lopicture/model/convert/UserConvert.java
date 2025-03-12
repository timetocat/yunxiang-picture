package com.lyx.lopicture.model.convert;

import com.lyx.lopicture.model.dto.user.UserAddRequest;
import com.lyx.lopicture.model.dto.user.UserEditRequest;
import com.lyx.lopicture.model.dto.user.UserUpdateRequest;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.LoginUserVO;
import com.lyx.lopicture.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    LoginUserVO mapToLoginUserVO(User user);

    UserVO mapToUserVO(User user);

    User mapToUser(UserAddRequest userAddRequest);

    User mapToUser(UserUpdateRequest userUpdateRequest);

    User mapToUser(UserEditRequest userEditRequest);
}
