package com.lyx.lopicture.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserAddRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserEditRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserQueryRequest;
import com.lyx.lopicture.model.entity.SpaceUser;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.SpaceUserVO;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-03-18 17:57:26
 */
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 添加空间用户关联
     *
     * @param spaceUserAddRequest
     * @return
     */
    Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 删除空间用户关联
     *
     * @param id
     * @param loginUser
     * @return
     */
    Boolean deleteSpaceUser(Long id, User loginUser);

    /**
     * 获取空间用户关联信息
     *
     * @param spaceUserQueryRequest
     * @param loginUser
     * @return
     */
    SpaceUser getSpaceUserInfo(SpaceUserQueryRequest spaceUserQueryRequest, User loginUser);

    /**
     * 编辑空间用户关联信息
     *
     * @param spaceEditRequest
     * @param loginUser
     * @return
     */
    Boolean editSpaceUser(SpaceUserEditRequest spaceEditRequest, User loginUser);

    /**
     * 获取查询条件对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    LambdaQueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取空间用户关联列表
     *
     * @param spaceUserList
     * @param loginUser
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList, User loginUser);
}
