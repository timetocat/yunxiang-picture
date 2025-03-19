package com.lyx.lopicture.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 空间成员响应类
 *
 * @param id         主键id
 * @param spaceId    空间 id
 * @param userId     用户 id
 * @param spaceRole  空间角色：viewer/editor/admin
 * @param createTime 创建时间
 * @param updateTime 更新时间
 * @param user       用户信息
 * @param space      空间信息
 */
public record SpaceUserVO(
        Long id,
        Long spaceId,
        Long userId,
        String spaceRole,
        Date createTime,
        Date updateTime,
        UserVO user,
        SpaceVO space
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
