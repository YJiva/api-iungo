package com.apiiungo.mapper;

import com.apiiungo.entity.PostAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostAdminMapper {
    List<PostAdmin> selectByCategory(@Param("categoryId") Long categoryId);

    int existsActive(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    int insert(PostAdmin admin);

    int disable(@Param("categoryId") Long categoryId, @Param("userId") Long userId);
}
