package com.lyx.lopicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.mapper.SpaceUserMapper;
import com.lyx.lopicture.model.convert.SpaceUserConvert;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserAddRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserEditRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserQueryRequest;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.entity.SpaceUser;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.SpaceRoleEnum;
import com.lyx.lopicture.model.vo.SpaceUserVO;
import com.lyx.lopicture.service.SpaceService;
import com.lyx.lopicture.service.SpaceUserService;
import com.lyx.lopicture.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
 * @createDate 2025-03-18 17:57:26
 */
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
        implements SpaceUserService {

    private static final SpaceUserConvert SPACE_USER_CONVERT = SpaceUserConvert.INSTANCE;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private SpaceService spaceService;

    /**
     * 添加空间用户关联
     *
     * @param spaceUserAddRequest
     * @return
     */
    @Override
    public Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        SpaceUser spaceUser = SPACE_USER_CONVERT.mapToSpaceUser(spaceUserAddRequest);
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    @Override
    public Boolean deleteSpaceUser(Long id, User loginUser) {
        checkPermissions(loginUser, id);
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 获取空间用户关联信息
     *
     * @param spaceUserQueryRequest
     * @param loginUser
     * @return
     */
    @Override
    public SpaceUser getSpaceUserInfo(SpaceUserQueryRequest spaceUserQueryRequest, User loginUser) {
        Long id = spaceUserQueryRequest.id();
        boolean idExist = ObjectUtil.isNotNull(id);
        String spaceRole = spaceUserQueryRequest.spaceRole();
        Long spaceId = spaceUserQueryRequest.spaceId();
        Long userId = spaceUserQueryRequest.userId();
        // 1. 校验是否存在团队空间
        boolean exist = this.lambdaQuery()
                .eq(SpaceUser::getSpaceId, spaceId)
                .exists();
        ThrowUtils.throwIf(!exist, ErrorCode.NOT_FOUND_ERROR, "该空间不存在");
        // 2. 校验权限
        exist = this.lambdaQuery()
                .eq(SpaceUser::getSpaceId, spaceId)
                .eq(SpaceUser::getUserId, loginUser.getId())
                .exists();
        ThrowUtils.throwIf(!exist || userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);
        // 3. 根据条件查询
        LambdaQueryChainWrapper<SpaceUser> wrapper = this.lambdaQuery()
                .eq(idExist, SpaceUser::getId, id);
        if (idExist) {
            return wrapper.one();
        }
        return wrapper
                .eq(SpaceUser::getSpaceId, spaceId)
                .eq(SpaceUser::getUserId, userId)
                .eq(CharSequenceUtil.isNotBlank(spaceRole), SpaceUser::getSpaceRole, spaceRole)
                .one();
    }

    /**
     * 编辑空间用户关联信息
     *
     * @param spaceEditRequest
     * @param loginUser
     * @return
     */
    @Override
    public Boolean editSpaceUser(SpaceUserEditRequest spaceEditRequest, User loginUser) {
        // 1. 校验权限
        checkPermissions(loginUser, spaceEditRequest.id());
        // 2. 编辑空间用户
        SpaceUser spaceUser = SPACE_USER_CONVERT.mapToSpaceUser(spaceEditRequest);
        boolean result = this.updateById(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 获取查询条件对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        LambdaQueryWrapper<SpaceUser> queryWrapper = Wrappers.lambdaQuery(SpaceUser.class);
        if (ObjectUtil.isNotNull(spaceUserQueryRequest)) {
            Long id = spaceUserQueryRequest.id();
            Long spaceId = spaceUserQueryRequest.spaceId();
            Long userId = spaceUserQueryRequest.userId();
            String spaceRole = spaceUserQueryRequest.spaceRole();
            queryWrapper.eq(ObjectUtil.isNotNull(id), SpaceUser::getId, id)
                    .eq(ObjectUtil.isNotNull(spaceId), SpaceUser::getSpaceId, spaceId)
                    .eq(ObjectUtil.isNotNull(userId), SpaceUser::getUserId, userId)
                    .eq(CharSequenceUtil.isNotBlank(spaceRole), SpaceUser::getSpaceRole, spaceRole);
        }
        return queryWrapper;
    }

    /**
     * 获取空间用户关联列表
     *
     * @param spaceUserList
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList, User loginUser) {
        // 1. 判断列表是否为空
        if (CollUtil.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        // 2. 获取用户 id 列表和空间 id 列表
        Set<Long> userIdSet = spaceUserList.stream()
                .map(SpaceUser::getUserId)
                .collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream()
                .map(SpaceUser::getSpaceId)
                .collect(Collectors.toSet());
        // 3. 批量查询用户和空间
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.listByIds(spaceIdSet)
                .stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 4. 转换成 VO 视图 并返回
        return spaceUserList.stream()
                .map(spaceUser -> {
                    Long userId = spaceUser.getUserId();
                    Long spaceId = spaceUser.getSpaceId();
                    User user = null;
                    if (userIdUserListMap.containsKey(userId)) {
                        user = userIdUserListMap.get(userId).get(0);
                    }
                    Space space = null;
                    if (spaceIdSpaceListMap.containsKey(spaceId)) {
                        space = spaceIdSpaceListMap.get(spaceId).get(0);
                    }
                    return SPACE_USER_CONVERT.mapToSpaceUserVO(spaceUser,
                            spaceService.getSpaceVO(space, null),
                            userService.getUserVO(user));
                })
                .toList();
    }


    /**
     * 检查权限
     *
     * @param loginUser 登录用户
     * @param id        主键id
     */
    private void checkPermissions(User loginUser, Long id) {
        SpaceUser spaceUser = this.lambdaQuery()
                .select(SpaceUser::getUserId, SpaceUser::getSpaceId)
                .eq(SpaceUser::getId, id)
                .one();
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR, "该空间用户不存在");
        // 1. 系统管理员最高权限
        if (userService.isAdmin(loginUser)) return;
        // 2. 校验是否为本人
        if (loginUser.getId().equals(spaceUser.getUserId())) return;
        // 3. 校验是否为管理员（空间管理员）
        boolean exists = this.lambdaQuery()
                .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                .eq(SpaceUser::getUserId, loginUser.getId())
                .eq(SpaceUser::getSpaceRole, SpaceRoleEnum.ADMIN.getValue())
                .exists();
        ThrowUtils.throwIf(!exists, ErrorCode.NO_AUTH_ERROR);
    }
}




