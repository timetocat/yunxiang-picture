package com.lyx.lopicture.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.manager.auth.model.SpaceUserAuthConfig;
import com.lyx.lopicture.manager.auth.model.SpaceUserPermissionConstant;
import com.lyx.lopicture.manager.auth.model.SpaceUserRole;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.entity.SpaceUser;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.SpaceRoleEnum;
import com.lyx.lopicture.model.enums.SpaceTypeEnum;
import com.lyx.lopicture.service.SpaceUserService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 空间成员权限管理
 */
@Component
@RequiredArgsConstructor
public class SpaceUserAuthManager {

    private final UserService userService;

    private final SpaceUserService spaceUserService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     *
     * @param spaceUserRole 空间成员角色
     * @return
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (CharSequenceUtil.isBlank(spaceUserRole)) {
            return Collections.emptyList();
        }
        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles().stream()
                .filter(r -> r.getKey().equals(spaceUserRole))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return Collections.emptyList();
        }
        return role.getPermissions();
    }

    /**
     * 获取权限列表
     *
     * @param space     空间
     * @param loginUser 登录用户
     * @return
     */
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return Collections.emptyList();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = this.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            }
            // 图片浏览权限
            return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
        }
        SpaceTypeEnum spaceTypeEnum = BaseValueEnum.getEnumByValue(SpaceTypeEnum.class, space.getSpaceType());
        // 根据空间获取对应权限
        switch (Objects.requireNonNull(spaceTypeEnum)) {
            case PRIVATE -> {
                // 私有空间，仅本人或管理员所有权限
                if (space.getUserId().equals(loginUser.getId()) ||
                        userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else return Collections.emptyList();
            }
            case TEAM -> {
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .select(SpaceUser::getSpaceRole)
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser != null) {
                    return this.getPermissionsByRole(spaceUser.getSpaceRole());
                } else return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
}
