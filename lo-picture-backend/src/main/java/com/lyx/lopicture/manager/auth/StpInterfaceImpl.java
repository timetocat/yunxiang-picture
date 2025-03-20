package com.lyx.lopicture.manager.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.lyx.lopicture.constant.UserConstant;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.manager.auth.model.SpaceUserPermissionConstant;
import com.lyx.lopicture.model.entity.Picture;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.entity.SpaceUser;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.SpaceRoleEnum;
import com.lyx.lopicture.model.enums.SpaceTypeEnum;
import com.lyx.lopicture.service.PictureService;
import com.lyx.lopicture.service.SpaceService;
import com.lyx.lopicture.service.SpaceUserService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    // 默认是 /api
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    private final UserService userService;

    private final SpaceService spaceService;

    private final SpaceUserService spaceUserService;

    private final PictureService pictureService;

    private final SpaceUserAuthManager spaceUserAuthManager;


    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1. 校验登录类型
        // 如果 loginType 不为 'space' 直接返回空列表
        if (!StpKit.SPACE_TYPE.equals(loginType)) {
            return Collections.emptyList();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSION = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 2. 获取请求上下文
        SpaceUserAuthContext authContext = this.getAuthContextByRequest();
        // 如果字段都为空，表示查询公共图库，可以通过
        if (isAllFieldsNull(authContext)) return ADMIN_PERMISSION;
        // 3. 管理员权限处理（是否为管理员）
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId)
                .get(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        if (userService.isAdmin(loginUser)) {
            return ADMIN_PERMISSION;
        }
        Long userId = loginUser.getId();
        // 4. 获取 SpaceUser 对象，如果存在，则返回 SpaceUser 角色的权限列表
        SpaceUser spaceUser = authContext.getSpaceUser();
        if (spaceUser != null) {
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
        // 5. SpaceUser 对象不存在， 获取 spaceUserId，如果存在进行查询 获取 SpaceUser 角色的权限列表
        Long spaceUserId = authContext.getSpaceUserId();
        if (spaceUserId != null) {
            spaceUser = spaceUserService.lambdaQuery()
                    .select(SpaceUser::getSpaceId)
                    .eq(SpaceUser::getId, spaceUserId)
                    .one();
            ThrowUtils.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR, "未找到空间用户信息");
            spaceUser = spaceUserService.lambdaQuery()
                    .select(SpaceUser::getSpaceRole)
                    .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (spaceUser == null) {
                return Collections.emptyList();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
        // 6. 如果 spaceId 和 pictureId 都为空，默认为管理员权限
        Long spaceId = authContext.getSpaceId();
        Long pictureId = authContext.getPictureId();
        if (spaceId == null && pictureId == null) {
            return ADMIN_PERMISSION;
        }
        // 7. 获取 pictureId，如果存在，查询 spaceId
        if (pictureId != null) {
            Picture picture = pictureService.lambdaQuery()
                    .select(Picture::getSpaceId, Picture::getUserId)
                    .eq(Picture::getId, pictureId)
                    .one();
            ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR, "未找到图片信息");
            spaceId = picture.getSpaceId();
            // 公共图库，仅本人或管理员可操作
            if (ObjectUtil.isNull(spaceId)) {
                if (picture.getUserId().equals(userId)) {
                    return ADMIN_PERMISSION;
                }
                // 不是自己上传的图片，仅可查看
                return Collections
                        .singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
            }
        }
        // 8. 获取 spaceId ，如果存在，判断是否为私有私有空间，如果不是，配合 userId 获取 SpaceUser 角色的权限列表
        Space space = spaceService.lambdaQuery()
                .select(Space::getUserId, Space::getSpaceType)
                .eq(Space::getId, spaceId)
                .one();
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "未找到空间信息");
        // 判断是否为私有空间
        if (SpaceTypeEnum.PRIVATE.getValue().equals(space.getSpaceType())) {
            // 私有空间，仅本人或管理员有权限
            if (space.getUserId().equals(userId)) {
                return ADMIN_PERMISSION;
            } else return Collections.emptyList();
        } else { // 团队空间
            spaceUser = spaceUserService.lambdaQuery()
                    .select(SpaceUser::getSpaceRole)
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (spaceUser == null) {
                return Collections.emptyList();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
    }

    /**
     * 本项目中不使用。返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    /**
     * 从请求中获取上下文对象
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        SpaceUserAuthContext authRequest;
        // 获取请求参数
        if (ContentType.JSON.getValue().equals(contentType)) {
            String body = ServletUtil.getBody(request);
            authRequest = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            authRequest = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }
        // 根据请求路径区分 id 字段的含义
        Long id = authRequest.getId();
        if (ObjUtil.isNotNull(id)) {
            // 获取到请求路径的业务前缀，/api/picture/aaa?a=1
            String requestURI = request.getRequestURI();
            // 先替换掉上下文，剩下的就是前缀
            String partURI = requestURI.replace(contextPath + "/", "");
            // 获取前缀的第一个斜杠前的字符串
            String moduleName = StrUtil.subBefore(partURI, "/", false);
            switch (moduleName) {
                case "picture" -> authRequest.setPictureId(id);
                case "spaceUser" -> authRequest.setSpaceUserId(id);
                case "space" -> authRequest.setSpaceId(id);
            }
        }
        return authRequest;
    }

    /**
     * 判断对象的所有字段是否为空
     *
     * @param object
     * @return
     */
    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true; // 对象本身为空
        }
        // 获取所有字段并判断是否所有字段都为空
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                // 获取字段值
                .map(field -> ReflectUtil.getFieldValue(object, field))
                // 检查是否所有字段都为空
                .allMatch(ObjectUtil::isEmpty);
    }

}
