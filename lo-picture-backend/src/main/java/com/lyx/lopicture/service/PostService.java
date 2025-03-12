package com.lyx.lopicture.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.post.PostAddRequest;
import com.lyx.lopicture.model.dto.post.PostEditRequest;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.dto.post.PostUpdateRequest;
import com.lyx.lopicture.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【post(帖子)】的数据库操作Service
 * @createDate 2025-02-18 16:04:53
 */
public interface PostService extends IService<Post> {

    /**
     * 添加帖子
     *
     * @param postAddRequest 帖子添加请求
     * @param loginUser      登录用户
     * @return 创建帖子主键id
     */
    Long addPost(PostAddRequest postAddRequest, User loginUser);

    /**
     * 删除帖子
     *
     * @param id   主键id
     * @param user 用户
     * @return 是否删除成功
     */
    Boolean deletePost(Long id, User user);

    /**
     * 更新帖子
     *
     * @param postUpdateRequest 帖子更新请求
     * @return 是否更新成功
     */
    Boolean updatePost(PostUpdateRequest postUpdateRequest);

    /**
     * 根据id获取帖子视图
     *
     * @param id        主键id
     * @param loginUser 登录用户（可以不登录）
     * @return 帖子视图
     */
    PostVO getPostVO(long id, User loginUser);

    /**
     * 获取帖子查询包装类
     *
     * @param postQueryRequest 帖子查询请求
     * @return 查询包装类
     */
    @Deprecated
    LambdaQueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 分页获取帖子
     *
     * @param postQueryRequest 帖子查询请求
     * @return 帖子分页信息
     */
    Page<Post> getPostPage(PostQueryRequest postQueryRequest);

    /**
     * 获取帖子封装分页信息
     *
     * @param postPage 帖子分页信息
     * @param request  http请求
     * @return 帖子封装分页信息
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);

    /**
     * 编辑帖子
     *
     * @param postEditRequest 编辑帖子请求
     * @param loginUser       登录用户
     * @return 是否编辑成功
     */
    Boolean editPost(PostEditRequest postEditRequest, User loginUser);

    /**
     * 判断帖子是否存在
     *
     * @param id 主键id
     */
    void checkPostExist(Long id);

    /**
     * 更新帖子点赞数
     *
     * @param postId 主键id
     * @param addNum 增加的点赞数
     * @return
     */
    Integer updatePostThumb(Long postId, Integer addNum);

    /**
     * 更新帖子收藏数
     *
     * @param postId 主键id
     * @param addNum 增加的收藏数
     * @return
     */
    Integer updatePostFavour(Long postId, int addNum);

}
