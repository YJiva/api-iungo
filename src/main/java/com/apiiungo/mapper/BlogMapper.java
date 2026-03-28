package com.apiiungo.mapper;

import com.apiiungo.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogMapper {

    int insertBlog(Blog blog);

    int updateBlog(Blog blog);

    Blog selectById(@Param("id") Long id);

    List<Blog> selectByUser(@Param("userId") Long userId);

    List<Blog> selectPublic();

    List<Blog> selectPublicByUser(@Param("userId") Long userId);

    // 查询所有已使用的标签（去重）
    List<String> selectAllTags();

    // 管理后台：查询全部博客
    List<Blog> selectAll();

    int deleteById(@Param("id") Long id);
}

