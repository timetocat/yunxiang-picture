package com.lyx.lopicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.manager.upload.picture.FilePictureUpload;
import com.lyx.lopicture.manager.upload.picture.PictureUploadTemplate;
import com.lyx.lopicture.manager.upload.picture.UrlPictureUpload;
import com.lyx.lopicture.mapper.PictureMapper;
import com.lyx.lopicture.model.convert.PictureConvert;
import com.lyx.lopicture.model.dto.file.UploadPictureResult;
import com.lyx.lopicture.model.dto.picture.PictureEditRequest;
import com.lyx.lopicture.model.dto.picture.PictureQueryRequest;
import com.lyx.lopicture.model.dto.picture.PictureUpdateRequest;
import com.lyx.lopicture.model.dto.picture.PictureUploadRequest;
import com.lyx.lopicture.model.entity.Picture;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.PictureVO;
import com.lyx.lopicture.model.vo.UserVO;
import com.lyx.lopicture.service.PictureService;
import com.lyx.lopicture.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-03-12 15:32:48
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    private static final PictureConvert PICTURE_CONVERT = PictureConvert.INSTANCE;

    @Resource
    private UserService userService;

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

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
        // 判断为新增还是修改
        Long pictureId = null;
        if (ObjectUtil.isNotEmpty(pictureUploadRequest)) {
            pictureId = pictureUploadRequest.id();
        }
        // 如果是更新，判断图片是否存在
        if (ObjectUtil.isNotEmpty(pictureId)) {
            // 仅本人或管理员可编辑图片
            checkPermissions(currentUser, pictureId);
        }
        // 上传图片，得到图片信息
        // 按照用户 id 划分目录
        String uploadPathPrefix = String.format("public/%s", currentUser.getId());
        // 根据 inputSource 的类型区分上传方式
        PictureUploadTemplate pictureUploadTemplate = inputSource instanceof String ?
                urlPictureUpload : filePictureUpload;
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        // 支持外层传递图片名称
        String picName = uploadPictureResult.getPicName();
        if (ObjectUtil.isNotEmpty(pictureUploadRequest) &&
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
        // 操作数据库
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (ObjectUtil.isNotEmpty(pictureId)) {
            // 如果是更新，需要补充 id
            picture.setId(pictureId);
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败，数据库操作失败");
        return PICTURE_CONVERT.mapToPictureVO(picture,
                userService.getUserVO(userService.getById(picture.getUserId())));
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
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片编辑失败");
        return true;
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
                .select(Picture::getUserId)
                .eq(Picture::getId, id)
                .one();
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
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
}




