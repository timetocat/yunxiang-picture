package com.lyx.lopicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.mapper.SpaceMapper;
import com.lyx.lopicture.model.dto.space.analyze.*;
import com.lyx.lopicture.model.entity.Picture;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.space.analyze.*;
import com.lyx.lopicture.service.PictureService;
import com.lyx.lopicture.service.SpaceAnalyzeService;
import com.lyx.lopicture.service.SpaceService;
import com.lyx.lopicture.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpaceAnalyzeServiceImpl extends ServiceImpl<SpaceMapper, Space>
        implements SpaceAnalyzeService {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private PictureService pictureService;

    /**
     * 获取空间使用情况
     *
     * @param spaceUsageAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
                                                          User loginUser) {
        // 1. 权限校验
        checkPermissions(spaceUsageAnalyzeRequest, loginUser);
        // 构造结果构造器
        SpaceUsageAnalyzeResponse.SpaceUsageAnalyzeResponseBuilder responseBuilder =
                SpaceUsageAnalyzeResponse.builder();
        // 2. 判断是否为全空间或公共图库
        // 2.1 全空间或公共图库使用 Picture 查询
        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            LambdaQueryWrapper<Picture> pictureQueryWrapper = Wrappers.lambdaQuery(Picture.class)
                    .select(Picture::getPicSize);
            // 填充查询条件
            this.fillAnalyzeQueryWrapper(spaceUsageAnalyzeRequest, pictureQueryWrapper);
            List<Object> pictureSizeList = pictureService.getBaseMapper()
                    .selectObjs(pictureQueryWrapper);
            long usedSize = pictureSizeList.stream().mapToLong(obj -> (long) obj).sum();
            long usedCount = pictureSizeList.size();
            responseBuilder.usedSize(usedSize).usedCount(usedCount);
            // 公共图库（或者全部空间）无数量和容量限制、也没有比例
        } else { // 2.2 私有空间使用 Space 查询
            Space space = spaceService.lambdaQuery()
                    .select(Space::getTotalCount, Space::getTotalSize, Space::getMaxCount, Space::getMaxSize)
                    .eq(Space::getId, spaceUsageAnalyzeRequest.getSpaceId())
                    .one();
            // 计算比例
            double sizeUsageRatio = NumberUtil
                    .round(space.getTotalSize() * 100.0 / space.getMaxSize(), 2).doubleValue();
            double countUsageRatio = NumberUtil
                    .round(space.getTotalCount() * 100.0 / space.getMaxCount(), 2).doubleValue();
            responseBuilder.usedCount(space.getTotalCount())
                    .usedSize(space.getTotalSize())
                    .maxSize(space.getMaxSize())
                    .maxCount(space.getMaxCount())
                    .sizeUsageRatio(sizeUsageRatio)
                    .countUsageRatio(countUsageRatio);
        }
        // 3. 返回结果
        return responseBuilder.build();
    }

    /**
     * 获取空间分类情况
     *
     * @param spaceCategoryAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        // 1. 权限校验
        checkPermissions(spaceCategoryAnalyzeRequest, loginUser);
        // 2. 返回查询结果
        return pictureService
                .getAnalyzeGroupByCategory(spaceCategoryAnalyzeRequest);
    }

    /**
     * 获取空间标签情况
     *
     * @param spaceTagAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        // 1. 校验权限
        checkPermissions(spaceTagAnalyzeRequest, loginUser);
        // 2. 构造查询参数并查询
        LambdaQueryWrapper<Picture> pictureQueryWrapper = Wrappers.lambdaQuery(Picture.class);
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, pictureQueryWrapper);
        // 查询所有符合条件的标签
        List<Picture> pictureList = pictureService.list(pictureQueryWrapper.select(Picture::getTags));
        Map<String, Long> tagCountMap = pictureList.stream()
                .filter(ObjectUtil::isNotNull)
                .map(Picture::getTags)
                .filter(CollUtil::isNotEmpty)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
        // 4. 转换为响应对象并返回（进行排序）
        return tagCountMap.entrySet().stream()
                .map(entry -> SpaceTagAnalyzeResponse.builder()
                        .tag(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted((o1, o2) -> o2.count().compareTo(o1.count()))
                .toList();
    }

    /**
     * 获取空间大小情况
     *
     * @param spaceSizeAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        // 1. 校验权限
        checkPermissions(spaceSizeAnalyzeRequest, loginUser);
        // 2. 构造查询条件
        LambdaQueryWrapper<Picture> pictureQueryWrapper = Wrappers.lambdaQuery(Picture.class);
        fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, pictureQueryWrapper);
        // 3. 查询所有符合条件的图片大小
        List<Long> picSizeList = pictureService.list(pictureQueryWrapper.select(Picture::getPicSize))
                .stream()
                .map(Picture::getPicSize)
                .filter(ObjectUtil::isNotNull)
                .toList();
        // 4. 统计定义范围 (注意有序)
        var sizeRanges = new LinkedHashMap<String, Long>() {{
            put("< 100 KB", 0L);
            put("100 KB ~ 500 KB", 0L);
            put("500 KB ~ 1 MB", 0L);
            put("> 1 MB", 0L);
        }};
        final long one_kb = 1024L;
        picSizeList.forEach(picSize -> {
            if (picSize < 100 * one_kb) {
                sizeRanges.merge("< 100 KB", 1L, Long::sum);
            } else if (picSize <= 500 * one_kb) {
                sizeRanges.merge("100 KB ~ 500 KB", 1L, Long::sum);
            } else if (picSize <= one_kb * one_kb) {
                sizeRanges.merge("500 KB ~ 1 MB", 1L, Long::sum);
            } else {
                sizeRanges.merge("> 1 MB", 1L, Long::sum);
            }
        });
        // 5. 转换为响应对象并返回
        return sizeRanges.entrySet().stream()
                .map(entry -> SpaceSizeAnalyzeResponse.builder()
                        .sizeRange(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
    }

    /**
     * 空间用户上传行为分析情况
     *
     * @param spaceUserAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest,
                                                              User loginUser) {
        // 1. 校验权限
        checkPermissions(spaceUserAnalyzeRequest, loginUser);
        // 2. 返回查询结果
        return pictureService.getAnalyzeGroupByUser(spaceUserAnalyzeRequest);
    }

    /**
     * 获取空间排行情况
     *
     * @param spaceRankAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        return spaceService.lambdaQuery()
                .select(Space::getId, Space::getSpaceName,
                        Space::getUserId, Space::getTotalSize)
                .orderByDesc(Space::getTotalSize)
                .page(new Page<>(1, spaceRankAnalyzeRequest.getTopN()))
                .getRecords();
    }

    /**
     * 获取空间等级情况
     *
     * @param spaceLevelAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceLevelAnalyzeResponse> getSpaceLevelAnalyze(SpaceLevelAnalyzeRequest spaceLevelAnalyzeRequest, User loginUser) {
        return spaceService.getAnalyzeByLevel(spaceLevelAnalyzeRequest);
    }

    /**
     * 获取空间审核情况
     *
     * @param spaceReviewAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceReviewAnalyzeResponse> getSpaceReviewAnalyze(SpaceReviewAnalyzeRequest spaceReviewAnalyzeRequest, User loginUser) {
        return pictureService.getAnalyzeByReview(spaceReviewAnalyzeRequest);
    }

    private void checkPermissions(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {
        // 全空间分析或者公共图库权限校验：仅管理员可访问
        if (spaceAnalyzeRequest.isQueryPublic() || spaceAnalyzeRequest.isQueryAll()) {
            ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);
        } else {
            spaceService.checkPermissions(loginUser, spaceAnalyzeRequest.getSpaceId());
        }
    }

    /**
     * 根据请求对象封装查询条件
     *
     * @param spaceAnalyzeRequest
     * @param queryWrapper
     */
    private void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest,
                                         LambdaQueryWrapper<Picture> queryWrapper) {
        if (spaceAnalyzeRequest.isQueryAll()) return;
        // 公共图库
        if (spaceAnalyzeRequest.isQueryPublic()) {
            queryWrapper.isNull(Picture::getSpaceId);
            return;
        }
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        if (ObjectUtil.isNotNull(spaceId)) { // 私有图库
            queryWrapper.eq(Picture::getSpaceId, spaceId);
            return;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
    }

}
