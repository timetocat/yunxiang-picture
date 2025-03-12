package com.lyx.lopicture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.picture.PictureQueryRequest;
import com.lyx.lopicture.model.entity.Picture;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 * @description 针对表【picture(图片)】的数据库操作Mapper
 * @createDate 2025-03-12 15:32:48
 * @Entity com.lyx.lopicture.model.entity.Picture
 */
public interface PictureMapper extends BaseMapper<Picture> {

    Page<Picture> selectPage(Page<Picture> page, @Param("query") PictureQueryRequest pictureQueryRequest);

}




