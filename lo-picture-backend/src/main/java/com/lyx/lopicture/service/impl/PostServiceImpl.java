package com.lyx.lopicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.mapper.PostFavourMapper;
import com.lyx.lopicture.mapper.PostMapper;
import com.lyx.lopicture.mapper.PostThumbMapper;
import com.lyx.lopicture.model.convert.PostConvert;
import com.lyx.lopicture.model.dto.post.PostAddRequest;
import com.lyx.lopicture.model.dto.post.PostEditRequest;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.dto.post.PostUpdateRequest;
import com.lyx.lopicture.model.entity.Post;
import com.lyx.lopicture.model.entity.PostFavour;
import com.lyx.lopicture.model.entity.PostThumb;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.PostVO;
import com.lyx.lopicture.service.PostService;
import com.lyx.lopicture.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【post(帖子)】的数据库操作Service实现
 * @createDate 2025-02-18 16:04:53
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {

    private static final PostConvert POST_CONVERT = PostConvert.INSTANCE;

    @Resource
    private UserService userService;

    @Resource
    private PostThumbMapper postThumbMapper;

    @Resource
    private PostFavourMapper postFavourMapper;

    /**
     * 添加帖子
     *
     * @param postAddRequest 帖子添加请求
     * @param loginUser      登录用户
     * @return 创建帖子主键id
     */
    @Override
    public Long addPost(PostAddRequest postAddRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Post post = POST_CONVERT.mapToPost(postAddRequest);
        post.setUserId(loginUser.getId());
        boolean result = this.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return post.getId();
    }

    /**
     * 删除帖子
     *
     * @param id   主键id
     * @param user 用户
     * @return 是否删除成功
     */
    @Override
    public Boolean deletePost(Long id, User user) {
        // 判断是否存在
        Post oldPost = this.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return this.removeById(id);
    }

    /**
     * 更新帖子
     *
     * @param postUpdateRequest 帖子更新请求
     * @return 是否更新成功
     */
    @Override
    public Boolean updatePost(PostUpdateRequest postUpdateRequest) {
        checkPostExist(postUpdateRequest.id());
        Post post = POST_CONVERT.mapToPost(postUpdateRequest);
        return this.updateById(post);
    }

    /**
     * 根据id获取帖子视图
     *
     * @param id        主键id
     * @param loginUser 登录用户（可以不登录）
     * @return 帖子视图
     */
    @Override
    public PostVO getPostVO(long id, User loginUser) {
        Post post = this.getById(id);
        PostVO postVO = POST_CONVERT.mapToPostVO(post);
        // 1. 关联查询用户信息
        Long userId = post.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        postVO.setUser(userService.getUserVO(user));
        // 2. 已登录，获取用户点赞、收藏状态
        if (loginUser != null) {
            Long postId = post.getId();
            // 获取点赞
            Long loginUserId = loginUser.getId();
            postVO.setHasThumb(postThumbMapper
                    .exists(Wrappers.lambdaQuery(PostThumb.class)
                            .in(PostThumb::getPostId, postId)
                            .eq(PostThumb::getUserId, loginUserId)));
            // 获取收藏
            postVO.setHasFavour(postFavourMapper
                    .exists(Wrappers.lambdaQuery(PostFavour.class)
                            .in(PostFavour::getPostId, postId)
                            .eq(PostFavour::getUserId, loginUserId)));
        }
        return postVO;
    }

    /**
     * 获取帖子查询包装类
     *
     * @param postQueryRequest 帖子查询请求
     * @return 查询包装类
     */
    @Deprecated
    @Override
    public LambdaQueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        LambdaQueryWrapper<Post> queryWrapper = Wrappers.lambdaQuery(Post.class);
        if (postQueryRequest != null) {
            // 拼接查询条件
            String searchText = postQueryRequest.getSearchText();
            if (CharSequenceUtil.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like(Post::getTitle, searchText).or().like(Post::getContent, searchText));
            }
            String title = postQueryRequest.getTitle();
            queryWrapper.like(CharSequenceUtil.isNotBlank(title), Post::getTitle, title);
            String content = postQueryRequest.getContent();
            queryWrapper.like(CharSequenceUtil.isNotBlank(content), Post::getContent, content);
            List<String> tags = postQueryRequest.getTags();
            if (CollUtil.isNotEmpty(tags)) {
                for (String tag : tags) {
                    queryWrapper.like(Post::getTags, "%" + tag + "%");
                }
            }
            Long notId = postQueryRequest.getNotId();
            queryWrapper.ne(ObjectUtil.isNotNull(notId), Post::getId, notId);
            Long id = postQueryRequest.getId();
            queryWrapper.eq(ObjectUtil.isNotNull(id), Post::getId, id);
            Long userId = postQueryRequest.getUserId();
/*            String sortField = postQueryRequest.getSortFieldPairs().get(0).getSortField();
            String sortOrder = postQueryRequest.getSortFieldPairs().get(0).getSortOrder();
            queryWrapper.eq(ObjectUtil.isNotNull(userId), Post::getUserId, userId);
            queryWrapper.apply(SqlUtils.validSortField(sortField), "ORDER BY {} {}", sortField,
                    CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? "ASC" : "DESC");*/
        }
        return queryWrapper;
    }

    /**
     * 分页获取帖子
     *
     * @param postQueryRequest 帖子查询请求
     * @return 帖子分页信息
     */
    @Override
    public Page<Post> getPostPage(PostQueryRequest postQueryRequest) {
        Page<Post> page = new Page<>(postQueryRequest.getCurrent(), postQueryRequest.getPageSize());
        return this.baseMapper.selectPage(page, postQueryRequest);
    }

    /**
     * 获取帖子封装分页信息
     *
     * @param postPage 帖子分页信息
     * @param request  http请求
     * @return 帖子封装分页信息
     */
    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        List<Post> postList = postPage.getRecords();
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollUtil.isEmpty(postList)) {
            return postVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = postList.stream()
                .map(Post::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> postIdHasThumbMap;
        Map<Long, Boolean> postIdHasFavourMap;
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            postIdHasThumbMap = Collections.emptyMap();
            postIdHasFavourMap = Collections.emptyMap();
        } else {
            Set<Long> postIdSet = postList.stream()
                    .map(Post::getId)
                    .collect(Collectors.toSet());
            // 获取点赞
            List<PostThumb> postThumbList = postThumbMapper.selectList(Wrappers.lambdaQuery(PostThumb.class)
                    .in(PostThumb::getPostId, postIdSet)
                    .eq(PostThumb::getUserId, loginUser.getId()));
            postIdHasThumbMap = postThumbList.stream()
                    .map(PostThumb::getPostId)
                    .collect(Collectors.toMap(
                            postId -> postId,
                            postId -> true,
                            (postId1, postId2) -> postId1
                    ));
            // 获取收藏
            List<PostFavour> postFavourList = postFavourMapper.selectList(Wrappers.lambdaQuery(PostFavour.class)
                    .in(PostFavour::getPostId, postIdSet)
                    .eq(PostFavour::getUserId, loginUser.getId()));
            postIdHasFavourMap = postFavourList.stream()
                    .map(PostFavour::getPostId)
                    .collect(Collectors.toMap(
                            postId -> postId,
                            postId -> true,
                            (postId1, postId2) -> postId1
                    ));
        }
        // 填充信息
        postVOPage.setRecords(postList.stream()
                .map(post -> {
                    PostVO postVO = POST_CONVERT.mapToPostVO(post);
                    Long userId = post.getUserId();
                    User user = null;
                    if (userIdUserListMap.containsKey(userId)) {
                        user = userIdUserListMap.get(userId).get(0);
                    }
                    postVO.setUser(userService.getUserVO(user));
                    postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
                    postVO.setHasFavour(postIdHasFavourMap.getOrDefault(post.getId(), false));
                    return postVO;
                }).toList());
        return postVOPage;
    }

    /**
     * 编辑帖子
     *
     * @param postEditRequest 编辑帖子请求
     * @param loginUser       登录用户
     * @return 是否编辑成功
     */
    @Override
    public Boolean editPost(PostEditRequest postEditRequest, User loginUser) {
        Post post = POST_CONVERT.mapToPost(postEditRequest);
        checkPostExist(postEditRequest.id());
        // 仅本人或管理员可编辑
        Post oldPost = this.getOne(Wrappers.lambdaQuery(Post.class)
                .select(Post::getUserId)
                .eq(Post::getId, postEditRequest.id()));
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return this.updateById(post);
    }


    /**
     * 判断帖子是否存在
     *
     * @param id 主键id
     */
    @Override
    public void checkPostExist(Long id) {
        // 判断帖子是否存在
        boolean exists = exists(Wrappers.lambdaQuery(Post.class).eq(Post::getId, id));
        ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
    }

    /**
     * 更新帖子点赞数
     *
     * @param postId 主键id
     * @param addNum 增加的点赞数
     * @return
     */
    @Override
    public Integer updatePostThumb(Long postId, Integer addNum) {
        return this.baseMapper.updateThumbNum(postId, addNum);
    }

    /**
     * 更新帖子收藏数
     *
     * @param postId 主键id
     * @param addNum 增加的收藏数
     * @return
     */
    @Override
    public Integer updatePostFavour(Long postId, int addNum) {
        return this.baseMapper.updateFavourNum(postId, addNum);
    }

}




