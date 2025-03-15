package com.lyx.lopicture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.space.SpaceQueryRequest;
import com.lyx.lopicture.model.entity.Space;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 * @description 针对表【space(空间)】的数据库操作Mapper
 * @createDate 2025-03-15 12:41:50
 * @Entity com.lyx.lopicture.model.entity.Space
 */
public interface SpaceMapper extends BaseMapper<Space> {

    Page<Space> selectPage(Page<Space> page, @Param("query") SpaceQueryRequest spaceQueryRequest);

    /**
     * 检查空间容量
     *
     * @param id 主键id
     * @return
     */
    int checkSpaceCapacity(@Param("id") Long id);

    boolean updateSpaceCapacity(@Param("id") Long id, @Param("size") Long size);

}




