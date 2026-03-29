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
    List<Post> selectByCategory(@Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectByCategoryWithFilter(@Param("categoryId") Long categoryId,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit,
                                          @Param("keyword") String keyword,
                                          @Param("essenceOnly") Integer essenceOnly);
    int updatePost(Post post);
    int incLikes(@Param("id") Long id);

    int decLikes(@Param("id") Long id);
    int incFavorites(@Param("id") Long id);
    int softDeleteById(@Param("id") Long id);
    int toggleTop(@Param("id") Long id);
    int toggleEssence(@Param("id") Long id);
    int decCommentsByPost(@Param("id") Long id, @Param("delta") int delta);
    int countByCategory(@Param("categoryId") Long categoryId);

    long countByAuthorId(@Param("authorId") Long authorId);

    List<Post> selectByAuthorId(@Param("authorId") Long authorId,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    long countLikedByUser(@Param("userId") Long userId, @Param("targetType") Long targetType);

    List<Post> selectLikedByUserPage(@Param("userId") Long userId,
                                     @Param("targetType") Long targetType,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
}
