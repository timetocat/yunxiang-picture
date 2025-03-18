package com.lyx.lopicture.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyx.lopicture.model.dto.space.SpaceAddRequest;
import com.lyx.lopicture.model.dto.space.SpaceEditRequest;
import com.lyx.lopicture.model.dto.space.SpaceQueryRequest;
import com.lyx.lopicture.model.dto.space.SpaceUpdateRequest;
import com.lyx.lopicture.model.dto.space.analyze.SpaceLevelAnalyzeRequest;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.SpaceVO;
import com.lyx.lopicture.model.vo.space.analyze.SpaceLevelAnalyzeResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-03-15 12:41:50
 */
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     *
     * @param spaceAddRequest 空间创建请求
     * @param loginUser       登录用户
     * @return 空间主键id
     */
    Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 删除空间
     *
     * @param id        主键id
     * @param loginUser 登录用户
     * @return 是否删除成功
     */
    Boolean deleteSpace(Long id, User loginUser);

    /**
     * 更新空间信息
     *
     * @param spaceUpdateRequest 更新空间信息
     * @param loginUser          登录用户
     * @return 更新是否成功
     */
    Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest, User loginUser);

    /**
     * 获取空间视图对象
     *
     * @param space   空间对象
     * @param request http请求
     * @return 空间视图
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间分页对象
     *
     * @param spaceQueryRequest 空间查询请求
     * @return 空间分页对象
     */
    Page<Space> getSpacePage(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取空间视图分页对象
     *
     * @param picturePage 空间分页对象
     * @param request     http请求
     * @return 空间视图分页对象
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> picturePage, HttpServletRequest request);

    /**
     * 编辑空间
     *
     * @param spaceEditRequest 空间编辑请求
     * @param loginUser        登录用户
     * @return 是否编辑成功
     */
    Boolean editSpace(SpaceEditRequest spaceEditRequest, User loginUser);

    /**
     * 判断空间是属于用户
     *
     * @param id        主键id
     * @param loginUser 登录用户
     * @return true：是，false：否
     */
    Boolean checkSpaceExistByUser(Long id, User loginUser);

    /**
     * 检测空间额度
     *
     * @param id 主键id
     * @return
     */
    String checkSpaceCapacity(Long id);

    /**
     * 更新空间额度
     *
     * @param id
     * @param size
     * @return
     */
    Boolean updateSpaceCapacity(Long id, Long size);

    /**
     * 根据等级分析空间
     *
     * @param spaceLevelAnalyzeRequest
     * @return
     */
    List<SpaceLevelAnalyzeResponse> getAnalyzeByLevel(SpaceLevelAnalyzeRequest spaceLevelAnalyzeRequest);

    /**
     * 判断空间是否存在
     *
     * @param id      主键id
     * @param throwEx 是否抛出异常
     * @return true：存在，false：不存在
     */
    Boolean checkSpaceExist(Long id, Boolean throwEx);

    /**
     * 填充空间信息
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 检查权限
     *
     * @param loginUser
     * @param id
     */
    void checkPermissions(User loginUser, Long id);

}
