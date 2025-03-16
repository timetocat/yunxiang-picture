package com.lyx.lopicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.api.imagesearch.ImageSearchApiFacade;
import com.lyx.lopicture.api.imagesearch.model.ImageSearchResult;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.manager.osManager.OsManager;
import com.lyx.lopicture.manager.upload.picture.FilePictureUpload;
import com.lyx.lopicture.manager.upload.picture.PictureUploadTemplate;
import com.lyx.lopicture.manager.upload.picture.UrlPictureUpload;
import com.lyx.lopicture.mapper.PictureMapper;
import com.lyx.lopicture.model.convert.PictureConvert;
import com.lyx.lopicture.model.dto.file.UploadPictureResult;
import com.lyx.lopicture.model.dto.picture.*;
import com.lyx.lopicture.model.entity.Picture;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.PictureReviewStatusEnum;
import com.lyx.lopicture.model.vo.PictureVO;
import com.lyx.lopicture.model.vo.UserVO;
import com.lyx.lopicture.service.PictureService;
import com.lyx.lopicture.service.SpaceService;
import com.lyx.lopicture.service.UserService;
import com.lyx.lopicture.utils.PictureUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-03-12 15:32:48
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    private static final PictureConvert PICTURE_CONVERT = PictureConvert.INSTANCE;

    @Value("${default.os-internal}")
    private boolean isIntranet;

    @Resource
    private UserService userService;

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

    @Resource
    private OsManager osManager;

    @Resource
    private SpaceService spaceService;

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 上传图片
     *
     * @param inputSource          文件输入源
     * @param pictureUploadRequest 图片上传请求
     * @param currentUser          当前用户
     * @return
     */
    @Override
    public PictureVO uploadPicture(Object inputSource,
                                   PictureUploadRequest pictureUploadRequest,
                                   User currentUser) {
        // 校验上传用户（登录用户）
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NO_AUTH_ERROR);
        Long spaceId = pictureUploadRequest.spaceId();
        if (ObjectUtil.isNotNull(spaceId)) {
            ThrowUtils.throwIf(!spaceService.checkSpaceExistByUser(spaceId, currentUser),
                    ErrorCode.NO_AUTH_ERROR, "空间不存在或无权限");
            String result = spaceService.checkSpaceCapacity(spaceId);
            ThrowUtils.throwIf(!CharSequenceUtil.equals(result, "OK"), ErrorCode.OPERATION_ERROR, result);
        }
        // 判断为新增还是修改
        Long pictureId = null;
        if (ObjectUtil.isNotNull(pictureUploadRequest)) {
            pictureId = pictureUploadRequest.id();
        }
        // 如果是更新，判断图片是否存在
        if (ObjectUtil.isNotNull(pictureId)) {
            // 仅本人或管理员可编辑图片
            checkPermissions(currentUser, pictureId);
        }
        // 上传图片，得到图片信息
        // 按照用户 id 划分目录
        String uploadPathPrefix = null;
        if (ObjectUtil.isNull(pictureId)) {
            uploadPathPrefix = String.format("public/%s", currentUser.getId());
        } else {
            uploadPathPrefix = String.format("space/%s", spaceId);
        }
        // 根据 inputSource 的类型区分上传方式
        PictureUploadTemplate pictureUploadTemplate = inputSource instanceof String ?
                urlPictureUpload : filePictureUpload;
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setSpaceId(spaceId);
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        // 支持外层传递图片名称
        String picName = uploadPictureResult.getPicName();
        if (ObjectUtil.isNotNull(pictureUploadRequest) &&
                CharSequenceUtil.isNotBlank(pictureUploadRequest.picName())) {
            picName = pictureUploadRequest.picName();
        }
        picture.setName(picName);
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(currentUser.getId());
        picture.setCategory(pictureUploadRequest.category());
        picture.setTags(pictureUploadRequest.tags());
        picture.setPicColor(uploadPictureResult.getPicColor());
        // 补充审核参数
        this.fillReviewParams(picture, currentUser);
        // 操作数据库
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (ObjectUtil.isNotNull(pictureId)) {
            // 如果是更新，需要补充 id
            picture.setId(pictureId);
        }
        transactionTemplate.execute(status -> {
            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败，数据库操作失败");
            if (ObjectUtil.isNotNull(spaceId)) {
                Boolean update = spaceService.updateSpaceCapacity(spaceId, picture.getPicSize());
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "空间容量更新失败");
            }
            return null;
        });
        return PICTURE_CONVERT.mapToPictureVO(picture, null);
    }

    /**
     * 删除图片
     *
     * @param id        图片id
     * @param loginUser 登录用户
     * @return 是否删除成功
     */
    @Override
    public Boolean delete(Long id, User loginUser) {
        checkPermissions(loginUser, id);
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片删除失败");
        // 清理图片资源
        this.clearPictureFile(id);
        return true;
    }

    /**
     * 更新图片
     *
     * @param pictureUpdateRequest 图片更新请求
     * @param loginUser            登录用户
     * @return 是否更新成功
     */
    @Override
    public Boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, User loginUser) {
        // 判断图片是否存在
        checkPictureExist(pictureUpdateRequest.id(), true);
        Picture picture = PICTURE_CONVERT.mapToPicture(pictureUpdateRequest);
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片更新失败");
        return true;
    }

    /**
     * 获取图片视图对象
     *
     * @param picture
     * @param request
     * @return
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        Long userId = picture.getUserId();
        UserVO userVO = null;
        if (userId != null && userId > 0) {
            userVO = userService.getUserVO(userService.getById(userId));
        }
        return PICTURE_CONVERT.mapToPictureVO(picture, userVO);
    }

    @Override
    public Page<Picture> getPicturePage(PictureQueryRequest pictureQueryRequest) {
        Page<Picture> page = new Page<>(pictureQueryRequest.getCurrent(), pictureQueryRequest.getPageSize());
        return this.baseMapper.selectPage(page, pictureQueryRequest);
    }

    /**
     * 获取图片分页视图对象
     *
     * @param picturePage
     * @param request
     * @return
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(),
                picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        Set<Long> userIdSet = pictureList.stream()
                .map(Picture::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        pictureVOPage.setRecords(pictureList.stream()
                .map(picture -> PICTURE_CONVERT.mapToPictureVO(picture,
                        userService.getUserVO(userIdUserListMap.get(picture.getUserId()).get(0))))
                .collect(Collectors.toList()));
        return pictureVOPage;
    }

    /**
     * 编辑图片
     *
     * @param pictureEditRequest 图片编辑请求
     * @param loginUser          登录用户
     * @return 是否编辑成功
     */
    @Override
    public Boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        checkPermissions(loginUser, pictureEditRequest.id());
        Picture picture = PICTURE_CONVERT.mapToPicture(pictureEditRequest);
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片编辑失败");
        return true;
    }

    @Override
    public Boolean doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 1. 判断图片是否存在
        Long pictureId = pictureReviewRequest.id();
        checkPictureExist(pictureId, true);
        // 2. 校验审核状态是否重复，不允许重复审核
        Picture oldPicture = this.lambdaQuery()
                .select(Picture::getReviewStatus)
                .eq(Picture::getId, pictureId)
                .one();
        if (oldPicture.getReviewStatus().equals(pictureReviewRequest.reviewStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复审核操作");
        }
        Picture picture = PICTURE_CONVERT.mapToPicture(pictureReviewRequest);
        picture.setReviewTime(new Date());
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片审核操作失败");
        return true;
    }

    /**
     * 上传图片（批量）
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    @Override
    public int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 前缀名称默认等于搜索词
        String namePrefix = pictureUploadByBatchRequest.namePrefix();
        String searchText = pictureUploadByBatchRequest.searchText();
        Integer count = pictureUploadByBatchRequest.count();
        if (CharSequenceUtil.isBlank(namePrefix)) {
            namePrefix = searchText;
        }
        // 抓取内容
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取页面失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        // 解析内容
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isEmpty(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
        }
        Elements imgElementList = div.select("img.mimg");
        // 遍历元素，依次处理上传图片
        int uploadCount = 0;
        for (Element imgElement : imgElementList) {
            String fileUrl = imgElement.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过：{}", fileUrl);
                continue;
            }
            // 处理图片的地址，防止转义或者和对象存储冲突的问题
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            // 上传图片
            PictureUploadRequest pictureUploadRequest = PictureUploadRequest.builder()
                    .fileUrl(fileUrl)
                    .picName(namePrefix + (uploadCount + 1))
                    .category(pictureUploadByBatchRequest.category())
                    .tags(pictureUploadByBatchRequest.tags())
                    .build();
            try {
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info("图片上传成功，id = {}", pictureVO.id());
                uploadCount++;
            } catch (Exception e) {
                log.error("图片上传失败", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }

    /**
     * 以图搜图
     *
     * @param searchPictureByPictureRequest 以图搜图请求
     * @return
     */
    @Override
    public List<ImageSearchResult> searchPictureByPicture(SearchPictureByPictureRequest searchPictureByPictureRequest) {
        Long pictureId = searchPictureByPictureRequest.pictureId();
        checkPictureExist(pictureId, true);
        Picture picture = this.lambdaQuery()
                .select(Picture::getUrl)
                .eq(Picture::getId, pictureId)
                .one();
        return ImageSearchApiFacade.searchImage(osManager.processPictureFormat(picture.getUrl()), isIntranet);
    }

    /**
     * 以颜色搜图
     *
     * @param searchPictureByColorRequest 以颜色搜图请求
     * @param loginUser                   登录用户
     * @return 图片视图对象列表
     */
    @Override
    public List<PictureVO> searchPictureByColor(SearchPictureByColorRequest searchPictureByColorRequest, User loginUser) {
        Long spaceId = searchPictureByColorRequest.spaceId();
        String picColor = searchPictureByColorRequest.picColor();
        boolean isPrivateSpace = ObjectUtil.isNotNull(spaceId);
        // 1. 校验空间权限
        ThrowUtils.throwIf(isPrivateSpace && !spaceService.checkSpaceExistByUser(spaceId, loginUser),
                ErrorCode.NO_AUTH_ERROR, "空间不存在或无权限");
        // 2. 查询该空间下的所有图片
        LambdaQueryChainWrapper<Picture> queryChainWrapper = this.lambdaQuery()
                .isNotNull(Picture::getPicColor);
        if (isPrivateSpace) {
            queryChainWrapper = queryChainWrapper
                    .eq(Picture::getSpaceId, spaceId);
        } else { // 显示指定 is null 是因为 mysql8 会走索引
            queryChainWrapper = queryChainWrapper
                    .isNull(Picture::getSpaceId);
        }
        Page<Picture> page = queryChainWrapper
                .page(new Page<>(1, 512));
        if (page.getTotal() == 0) {
            return Collections.emptyList();
        }
        List<Picture> pictureList = page.getRecords();
        // 将颜色字符串转换为主色调
        Color targetColor = Color.decode(picColor);
        // 3. 计算相似度并排序并返回结果
        return pictureList.stream()
                .sorted(Comparator.comparingDouble(picture -> {
                    String hexColor = picture.getPicColor();
                    // 没有主色调的图片默认排序到最后
                    if (CharSequenceUtil.isBlank(hexColor)) {
                        return Double.MAX_VALUE;
                    }
                    return -PictureUtils.calculateSimilarity(targetColor, Color.decode(hexColor));
                }))
                .limit(12)
                .map(picture -> PICTURE_CONVERT.mapToPictureVO(picture, null))
                .collect(Collectors.toList());
    }

    /**
     * 编辑图片（批量）
     *
     * @param pictureEditByBatchRequest
     * @param loginUser
     * @return
     */
    @Override
    public Boolean editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        Long spaceId = pictureEditByBatchRequest.spaceId();
        // 1. 校验空间权限
        boolean isUseSpace = ObjectUtil.isNotNull(spaceId);
        boolean nonAdmin = !userService.isAdmin(loginUser);
        if (!isUseSpace) {
            ThrowUtils.throwIf(nonAdmin, ErrorCode.NO_AUTH_ERROR);
        } else {
            ThrowUtils.throwIf(!spaceService.checkSpaceExistByUser(spaceId, loginUser) && nonAdmin,
                    ErrorCode.NO_AUTH_ERROR, "空间不存在或无权限");
        }
        // 2. 获取图片列表（选择需要的字段）
        List<Picture> pictureList = this.lambdaQuery()
                .select(Picture::getId)
                .eq(isUseSpace, Picture::getSpaceId, spaceId)
                .in(Picture::getId, pictureEditByBatchRequest.pictureIdList())
                .list();
        if (CollUtil.isEmpty(pictureList)) {
            return true;
        }
        // 3. 更新分类和标签
        String category = pictureEditByBatchRequest.category();
        if (CharSequenceUtil.isNotBlank(category)) {
            pictureList.forEach(picture -> picture.setCategory(category));
        }
        List<String> tags = pictureEditByBatchRequest.tags();
        if (CollUtil.isNotEmpty(tags)) {
            pictureList.forEach(picture -> picture.setTags(tags));
        }
        // 4. 批量命名
        fillPictureWithNameRule(pictureList, pictureEditByBatchRequest.nameRule());
        // 5. 更新
        boolean result = this.updateBatchById(pictureList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片批量修改失败");
        return true;
    }

    /**
     * 清理图片文件
     *
     * @param id 主键id
     */
    @Async
    @Override
    public void clearPictureFile(Long id) {
        Picture picture = this.baseMapper.getDeletePictureById(id);
        String url = picture.getUrl();
        Long count = this.lambdaQuery()
                .eq(Picture::getUrl, url)
                .count();
        // 有不止一条记录用到了该图片，不清理
        if (count > 0) return;
        // 删除图片
        try {
            osManager.deleteObject(url);
            // 删除缩略图
            String thumbnailUrl = picture.getThumbnailUrl();
            if (CharSequenceUtil.isNotBlank(thumbnailUrl)) {
                osManager.deleteObject(thumbnailUrl);
            }
        } catch (Exception e) {
            // 可以清理失败，对象存储有兜底策略
            log.info("图片清理失败", e);
        }
    }

    /**
     * 检查图片权限（修改或删除）
     *
     * @param loginUser 登录用户
     * @param id        主键id
     */
    private void checkPermissions(User loginUser, Long id) {
        // checkPictureExist(id, true);
        Picture picture = this.lambdaQuery()
                .select(Picture::getUserId, Picture::getSpaceId)
                .eq(Picture::getId, id)
                .one();
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        if (ObjectUtil.isNull(picture.getSpaceId())) {
            // 公共图库，仅本人或管理员可操作
            if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else { // 私有图库，仅本人可操作
            if (!picture.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }

    /**
     * 判断图片是否存在
     *
     * @param id      主键id
     * @param throwEx 是否抛出异常
     * @return true：存在，false：不存在
     */
    @Override
    public Boolean checkPictureExist(Long id, Boolean throwEx) {
        // 判断用户是否存在
        boolean exists = this.lambdaQuery()
                .eq(Picture::getId, id)
                .exists();
        if (!exists) {
            if (!throwEx) return false;
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }
        return true;
    }

    /**
     * 填充审核参数
     *
     * @param picture   图片
     * @param loginUser 登录用户
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            // 管理员审核，默认通过
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewMessage("管理员审核通过");
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
        } else {
            // 非管理员，无论是编辑还是创建默认都是待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    /**
     * nameRule 格式：图片{序号}
     *
     * @param pictureList
     * @param nameRule
     */
    private void fillPictureWithNameRule(List<Picture> pictureList, String nameRule) {
        if (StrUtil.isBlank(nameRule) || CollUtil.isEmpty(pictureList)) {
            return;
        }
        long count = 1;
        try {
            for (Picture picture : pictureList) {
                String pictureName = nameRule.replaceAll("\\{序号}", String.valueOf(count++));
                picture.setName(pictureName);
            }
        } catch (Exception e) {
            log.error("名称解析错误", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "名称解析错误");
        }
    }
}




