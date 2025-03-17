package com.lyx.lopicture.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyx.lopicture.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.lyx.lopicture.api.imagesearch.model.ImageSearchResult;
import com.lyx.lopicture.model.dto.picture.*;
import com.lyx.lopicture.model.dto.space.analyze.SpaceCategoryAnalyzeRequest;
import com.lyx.lopicture.model.dto.space.analyze.SpaceUserAnalyzeRequest;
import com.lyx.lopicture.model.entity.Picture;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.PictureVO;
import com.lyx.lopicture.model.vo.space.analyze.SpaceCategoryAnalyzeResponse;
import com.lyx.lopicture.model.vo.space.analyze.SpaceUserAnalyzeResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-03-12 15:32:48
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource          文件输入源
     * @param pictureUploadRequest 图片上传请求
     * @param currentUser          当前用户
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User currentUser);

    /**
     * 删除图片
     *
     * @param id        图片id
     * @param loginUser 登录用户
     * @return 是否删除成功
     */
    Boolean delete(Long id, User loginUser);

    /**
     * 更新图片
     *
     * @param pictureUpdateRequest 图片更新请求
     * @param loginUser            登录用户
     * @return 是否更新成功
     */
    Boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, User loginUser);

    /**
     * 获取图片视图对象
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片分页视图对象
     *
     * @param pictureQueryRequest 图片查询请求
     * @return
     */
    Page<Picture> getPicturePage(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片分页视图对象
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 编辑图片
     *
     * @param pictureEditRequest 图片编辑请求
     * @param loginUser          登录用户
     * @return 是否编辑成功
     */
    Boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 审核图片
     *
     * @param pictureReviewRequest 图片审核请求
     * @param loginUser            登录用户
     * @return 是否审核成功
     */
    Boolean doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 上传图片（批量）
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 以图搜图
     *
     * @param searchPictureByPictureRequest 以图搜图请求
     * @return
     */
    List<ImageSearchResult> searchPictureByPicture(SearchPictureByPictureRequest searchPictureByPictureRequest);

    /**
     * 以颜色搜图
     *
     * @param searchPictureByColorRequest 以颜色搜图请求
     * @param loginUser                   登录用户
     * @return 图片视图对象列表
     */
    List<PictureVO> searchPictureByColor(SearchPictureByColorRequest searchPictureByColorRequest, User loginUser);

    /**
     * 编辑图片（批量）
     *
     * @param pictureEditByBatchRequest
     * @param loginUser
     * @return
     */
    Boolean editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * 创建扩图任务
     *
     * @param createPictureOutPaintingTaskRequest 创建扩图任务
     * @param loginUser                           登录用户
     * @return
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);

    /**
     * 分组查询空间分类统计数据
     *
     * @param spaceCategoryAnalyzeRequest
     * @return
     */
    List<SpaceCategoryAnalyzeResponse> getAnalyzeGroupByCategory(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest);

    /**
     * 空间用户上传行为分析情况
     *
     * @param spaceUserAnalyzeRequest
     * @return
     */
    List<SpaceUserAnalyzeResponse> getAnalyzeGroupByUser(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest);

    /**
     * 清理图片文件
     *
     * @param id 主键id
     */
    void clearPictureFile(Long id);

    /**
     * 判断图片是否存在
     *
     * @param id      主键id
     * @param throwEx 是否抛出异常
     * @return true：存在，false：不存在
     */
    Boolean checkPictureExist(Long id, Boolean throwEx);

    /**
     * 填充审核参数
     *
     * @param picture   图片
     * @param loginUser 登录用户
     */
    void fillReviewParams(Picture picture, User loginUser);

}
