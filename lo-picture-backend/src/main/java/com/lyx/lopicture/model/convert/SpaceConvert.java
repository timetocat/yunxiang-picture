package com.lyx.lopicture.model.convert;

import com.lyx.lopicture.model.dto.space.SpaceAddRequest;
import com.lyx.lopicture.model.dto.space.SpaceEditRequest;
import com.lyx.lopicture.model.dto.space.SpaceUpdateRequest;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.vo.SpaceVO;
import com.lyx.lopicture.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpaceConvert {

    SpaceConvert INSTANCE = Mappers.getMapper(SpaceConvert.class);

    Space mapToSpace(SpaceAddRequest spaceAddRequest);

    Space mapToSpace(SpaceUpdateRequest spaceUpdateRequest);

    Space mapToSpace(SpaceEditRequest spaceEditRequest);

    @Mappings({
            @Mapping(source = "space.id", target = "id"),
            @Mapping(source = "space.createTime", target = "createTime"),
            @Mapping(source = "userVO", target = "user")
    })
    SpaceVO mapToSpaceVO(Space space, UserVO userVO);
}
