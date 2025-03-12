package com.lyx.lopicture.model.convert;

import com.lyx.lopicture.model.dto.picture.PictureEditRequest;
import com.lyx.lopicture.model.dto.picture.PictureUpdateRequest;
import com.lyx.lopicture.model.entity.Picture;
import com.lyx.lopicture.model.vo.PictureVO;
import com.lyx.lopicture.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PictureConvert {

    PictureConvert INSTANCE = Mappers.getMapper(PictureConvert.class);

    Picture mapToPicture(PictureUpdateRequest pictureUpdateRequest);

    Picture mapToPicture(PictureEditRequest pictureEditRequest);

    @Mappings({
            @Mapping(source = "picture.id", target = "id"),
            @Mapping(source = "picture.createTime", target = "createTime"),
            @Mapping(source = "userVO", target = "user")
    })
    PictureVO mapToPictureVO(Picture picture, UserVO userVO);


}
