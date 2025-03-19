package com.lyx.lopicture.model.convert;

import com.lyx.lopicture.model.dto.spaceuser.SpaceUserAddRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserEditRequest;
import com.lyx.lopicture.model.entity.SpaceUser;
import com.lyx.lopicture.model.vo.SpaceUserVO;
import com.lyx.lopicture.model.vo.SpaceVO;
import com.lyx.lopicture.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpaceUserConvert {

    SpaceUserConvert INSTANCE = Mappers.getMapper(SpaceUserConvert.class);

    SpaceUser mapToSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    SpaceUser mapToSpaceUser(SpaceUserEditRequest spaceUserEditRequest);

    @Mappings({
            @Mapping(source = "spaceUser.id", target = "id"),
            @Mapping(source = "spaceUser.createTime", target = "createTime"),
            @Mapping(source = "spaceUser.updateTime", target = "updateTime"),
            @Mapping(source = "spaceUser.userId", target = "userId"),
            @Mapping(source = "spaceVO", target = "space"),
            @Mapping(source = "userVO", target = "user")
    })
    SpaceUserVO mapToSpaceUserVO(SpaceUser spaceUser, SpaceVO spaceVO, UserVO userVO);

}
