package com.apiiungo.mapper;

import com.apiiungo.entity.Blog;
import com.apiiungo.vo.PublicBlogItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogMapper {

    int insertBlog(Blog blog);

    int updateBlog(Blog blog);

    Blog selectById(@Param("id") Long id);

    List<Blog> selectByUser(@Param("userId") Long userId);

    long countByUserId(@Param("userId") Long userId);

    List<Blog> selectByUserIdPage(@Param("userId") Long userId,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    long countLikedByUser(@Param("userId") Long userId, @Param("targetType") Long targetType);

    List<Blog> selectLikedByUserPage(@Param("userId") Long userId,
                                     @Param("targetType") Long targetType,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    List<Blog> selectPublic();

    List<Blog> selectPublicByUser(@Param("userId") Long userId);

    List<Blog> selectCircleBlogs(@Param("viewerId") Long viewerId);

    /** 仅互关可见(open_scope=3)：互关对象发帖 + 本人发帖 */
    List<Blog> selectCloseFriendBlogs(@Param("viewerId") Long viewerId);

    long countPublicBlogItems(@Param("pattern") String pattern);

    List<PublicBlogItem> selectPublicBlogItems(@Param("pattern") String pattern,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    Blog selectLatestDraftByUser(@Param("userId") Long userId);

    int clearDraftByUser(@Param("userId") Long userId);

    Integer selectPreferredScopeByUser(@Param("userId") Long userId);

    int updateScopeByUser(@Param("userId") Long userId, @Param("openScope") Integer openScope);

    // 查询所有已使用的标签（去重）
    List<String> selectAllTags();

    // 管理后台：查询全部博客
    List<Blog> selectAll();

    int deleteById(@Param("id") Long id);
}

