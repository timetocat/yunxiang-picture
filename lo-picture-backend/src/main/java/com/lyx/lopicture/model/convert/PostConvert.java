package com.lyx.lopicture.model.convert;

import com.lyx.lopicture.model.dto.post.PostAddRequest;
import com.lyx.lopicture.model.dto.post.PostEditRequest;
import com.lyx.lopicture.model.dto.post.PostUpdateRequest;
import com.lyx.lopicture.model.entity.Post;
import com.lyx.lopicture.model.vo.PostVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostConvert {
    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

    Post mapToPost(PostAddRequest postAddRequest);

    Post mapToPost(PostUpdateRequest postUpdateRequest);

    Post mapToPost(PostEditRequest postEditRequest);

    PostVO mapToPostVO(Post post);

}
