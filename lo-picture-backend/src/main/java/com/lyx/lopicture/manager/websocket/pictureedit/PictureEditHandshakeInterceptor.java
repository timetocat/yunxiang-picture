package com.lyx.lopicture.manager.websocket.pictureedit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import com.lyx.lopicture.manager.auth.SpaceUserAuthManager;
import com.lyx.lopicture.manager.auth.model.SpaceUserPermissionConstant;
import com.lyx.lopicture.model.entity.Picture;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.SpaceTypeEnum;
import com.lyx.lopicture.service.PictureService;
import com.lyx.lopicture.service.SpaceService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 图片编辑 WebSocket 拦截器，建立连接前要先校验
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PictureEditHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;

    private final PictureService pictureService;

    private final SpaceService spaceService;

    private final SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 建立连接前要先校验
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes 给 WebSocketSession 会话设置属性
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            // 从请求中获取参数
            String pictureId = httpServletRequest.getParameter("pictureId");
            if (CharSequenceUtil.isBlank(pictureId)) {
                log.error("pictureId is null, 拒绝握手");
                return false;
            }
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(httpServletRequest);
            if (ObjUtil.isEmpty(loginUser)) {
                log.error("user not logged in, 拒绝握手");
                return false;
            }
            // 校验用户是否有编辑当前图片的权限
            Picture picture = pictureService.lambdaQuery()
                    .select(Picture::getSpaceId)
                    .eq(Picture::getId, Long.valueOf(pictureId))
                    .one();
            if (ObjUtil.isEmpty(picture)) {
                log.error("picture not exist, 拒绝握手");
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (ObjUtil.isNotNull(spaceId)) {
                space = spaceService.getById(spaceId);
                if (ObjUtil.isEmpty(space)) {
                    log.error("space not exist, 拒绝握手");
                    return false;
                }
                if (!SpaceTypeEnum.TEAM.getValue().equals(space.getSpaceType())) {
                    log.error("图片所在空间不是团队空间，拒绝握手");
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if (!CollUtil.contains(permissionList, SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("user has no permission, 拒绝握手");
                return false;
            }
            // 设置用户登录信息等属性到 WebSocket 会话中
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId)); // 记得转换为 Long 类型
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
