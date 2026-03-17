package com.apiiungo.mapper;

import com.apiiungo.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    int insertPost(Post post);
    Post selectById(@Param("id") Long id);
    List<Post> selectRecent(@Param("offset") int offset, @Param("limit") int limit);
    int updatePost(Post post);
    int incLikes(@Param("id") Long id);
    int incFavorites(@Param("id") Long id);
}
