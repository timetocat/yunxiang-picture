package com.lyx.lopicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.mapper.SpaceMapper;
import com.lyx.lopicture.model.convert.SpaceConvert;
import com.lyx.lopicture.model.dto.space.SpaceAddRequest;
import com.lyx.lopicture.model.dto.space.SpaceEditRequest;
import com.lyx.lopicture.model.dto.space.SpaceQueryRequest;
import com.lyx.lopicture.model.dto.space.SpaceUpdateRequest;
import com.lyx.lopicture.model.dto.space.analyze.SpaceLevelAnalyzeRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserAddRequest;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.SpaceLevelEnum;
import com.lyx.lopicture.model.enums.SpaceRoleEnum;
import com.lyx.lopicture.model.enums.SpaceTypeEnum;
import com.lyx.lopicture.model.enums.spacelevel.SpaceLevel;
import com.lyx.lopicture.model.vo.SpaceVO;
import com.lyx.lopicture.model.vo.UserVO;
import com.lyx.lopicture.model.vo.space.analyze.SpaceLevelAnalyzeResponse;
import com.lyx.lopicture.service.SpaceService;
import com.lyx.lopicture.service.SpaceUserService;
import com.lyx.lopicture.service.UserService;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-03-15 12:41:50
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
        implements SpaceService {

    private static final SpaceConvert SPACE_CONVERT = SpaceConvert.INSTANCE;

    @Resource
    private UserService userService;

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private TransactionTemplate transactionTemplate;

    Map<Long, Object> lockMap = new ConcurrentHashMap<>();

    @Override
    public Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 1. 填充参数默认值
        Space space = SPACE_CONVERT.mapToSpace(spaceAddRequest);
        if (CharSequenceUtil.isBlank(space.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (space.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue().getValue());
        }
        // 填充容量大小
        this.fillSpaceBySpaceLevel(space);
        // 2. 校验权限，非管理员只能创建普通级别的空间
        boolean isAdmin = userService.isAdmin(loginUser);
        if (ObjectUtil.notEqual(SpaceLevelEnum.COMMON.getValue(),
                SpaceLevelEnum.getSpaceLevelInfo(space.getSpaceLevel()))) {
            ThrowUtils.throwIf(!isAdmin,
                    ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 3. 控制同一用户只能创建一个私有空间
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if (isAdmin) { // 管理员可以创建多空间
            return createSpace(space, userId);
        } else {
            Object lock = lockMap.computeIfAbsent(userId, key -> new Object());
            synchronized (lock) {
                try {
                    // 判断是否已有空间
                    boolean exists = this.lambdaQuery()
                            .eq(Space::getUserId, userId)
                            .eq(Space::getSpaceType, space.getSpaceType())
                            .exists();
                    // 已有空间，不能继续创建
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                    // 创建空间
                    return createSpace(space, userId);
                } finally {
                    lockMap.remove(userId);
                }
            }
        }
    }

    @Override
    public Boolean deleteSpace(Long id, User loginUser) {
        checkPermissions(loginUser, id);
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "空间删除失败");
        return true;
    }

    /**
     * 更新空间信息
     *
     * @param spaceUpdateRequest 更新空间信息
     * @param loginUser          登录用户
     * @return 更新是否成功
     */
    @Override
    public Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest, User loginUser) {
        checkSpaceExist(spaceUpdateRequest.id(), true);
        Space space = SPACE_CONVERT.mapToSpace(spaceUpdateRequest);
        this.fillSpaceBySpaceLevel(space);
        boolean result = this.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "空间更新失败");
        return true;
    }

    /**
     * 获取空间视图对象
     *
     * @param space   空间对象
     * @param request http请求
     * @return 空间视图
     */
    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        Long userId = space.getUserId();
        UserVO userVO = null;
        if (userId != null && userId > 0) {
            userVO = userService.getUserVO(userService.getById(userId));
        }
        return SPACE_CONVERT.mapToSpaceVO(space, userVO);
    }

    /**
     * 获取空间分页对象
     *
     * @param spaceQueryRequest 空间查询请求
     * @return 空间分页对象
     */
    @Override
    public Page<Space> getSpacePage(SpaceQueryRequest spaceQueryRequest) {
        Page<Space> page = new Page<>(spaceQueryRequest.getCurrent(), spaceQueryRequest.getPageSize());
        return this.baseMapper.selectPage(page, spaceQueryRequest);
    }

    /**
     * 获取空间视图分页对象
     *
     * @param picturePage 空间分页对象
     * @param request     http请求
     * @return 空间视图分页对象
     */
    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> picturePage, HttpServletRequest request) {
        List<Space> spaceList = picturePage.getRecords();
        Page<SpaceVO> SpaceVOPage = new Page<>(picturePage.getCurrent(),
                picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return SpaceVOPage;
        }
        Set<Long> userIdSet = spaceList.stream()
                .map(Space::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        SpaceVOPage.setRecords(spaceList.stream()
                .map(Space -> SPACE_CONVERT.mapToSpaceVO(Space,
                        userService.getUserVO(userIdUserListMap.get(Space.getUserId()).get(0))))
                .collect(Collectors.toList()));
        return SpaceVOPage;
    }

    /**
     * 编辑空间
     *
     * @param spaceEditRequest 空间编辑请求
     * @param loginUser        登录用户
     * @return 是否编辑成功
     */
    @Override
    public Boolean editSpace(SpaceEditRequest spaceEditRequest, User loginUser) {
        checkSpaceExist(spaceEditRequest.id(), true);
        Space space = SPACE_CONVERT.mapToSpace(spaceEditRequest);
        boolean result = this.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "空间编辑失败");
        return true;
    }

    /**
     * 判断空间是属于用户
     *
     * @param id        主键id
     * @param loginUser 登录用户
     * @return true：是，false：否
     */
    @Override
    public Boolean checkSpaceExistByUser(Long id, User loginUser) {
        return this.lambdaQuery()
                .eq(Space::getUserId, loginUser.getId())
                .eq(Space::getId, id)
                .exists();
    }

    /**
     * 检测空间额度（逻辑比较简单，有待更改）
     *
     * @param id 主键id
     * @return
     */
    @Override
    public String checkSpaceCapacity(Long id) {
        int result = this.baseMapper.checkSpaceCapacity(id);
        return switch (result) {
            case 0 -> "OK";
            case 1 -> "空间条数不足";
            case 2 -> "空间容量不足";
            default -> null;
        };
    }

    @Override
    public Boolean updateSpaceCapacity(Long id, Long size) {
        return this.baseMapper.updateSpaceCapacity(id, size);
    }

    /**
     * 根据等级分析空间
     *
     * @param spaceLevelAnalyzeRequest
     * @return
     */
    @Override
    public List<SpaceLevelAnalyzeResponse> getAnalyzeByLevel(SpaceLevelAnalyzeRequest spaceLevelAnalyzeRequest) {
        return this.baseMapper.getAnalyzeByLevel(spaceLevelAnalyzeRequest).stream()
                .map(SpaceLevelAnalyzeResponse.SpaceLevelAnalyzeInnerResponse::transform)
                .toList();
    }


    /**
     * 判断空间是否存在
     *
     * @param id      主键id
     * @param throwEx 是否抛出异常
     * @return true：存在，false：不存在
     */
    @Override
    public Boolean checkSpaceExist(Long id, Boolean throwEx) {
        // 判断用户是否存在
        boolean exists = this.lambdaQuery()
                .eq(Space::getId, id)
                .exists();
        if (!exists) {
            if (!throwEx) return false;
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        return true;
    }

    /**
     * 填充空间信息
     *
     * @param space
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        SpaceLevel spaceLevel = SpaceLevelEnum.getSpaceLevelInfo(space.getSpaceLevel());
        if (spaceLevel != null) {
            long maxCount = spaceLevel.getMaxCount();
            if (ObjectUtil.isNull(space.getMaxCount())) {
                space.setMaxCount(maxCount);
            }
            long maxSize = spaceLevel.getMaxSize();
            if (ObjectUtil.isNull(space.getMaxSize())) {
                space.setMaxSize(maxSize);
            }
        }
    }

    /**
     * 检查空间权限（修改或删除）
     *
     * @param loginUser 登录用户
     * @param id        主键id
     */
    @Override
    public void checkPermissions(User loginUser, Long id) {
        Space space = this.lambdaQuery()
                .select(Space::getUserId)
                .eq(Space::getId, id)
                .one();
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    /**
     * 创建空间
     *
     * @param space
     * @param userId
     * @return
     */
    @Nullable
    private Long createSpace(Space space, Long userId) {
        return transactionTemplate.execute(status -> {
            boolean result = this.save(space);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 创建成功后，如果是团队空间，关联新增团队成员记录
            if (SpaceTypeEnum.TEAM.getValue().equals(space.getSpaceType())) {
                spaceUserService.addSpaceUser(SpaceUserAddRequest.builder()
                        .userId(userId)
                        .spaceId(space.getId())
                        .spaceRole(SpaceRoleEnum.ADMIN.getValue())
                        .build());
            }
            return space.getId();
        });
    }

}


