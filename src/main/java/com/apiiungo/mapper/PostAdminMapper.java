package com.apiiungo.mapper;

import com.apiiungo.entity.PostAdmin;
import com.apiiungo.entity.PostAdminView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostAdminMapper {
    List<PostAdmin> selectByCategory(@Param("categoryId") Long categoryId);

    List<PostAdminView> selectViewByCategory(@Param("categoryId") Long categoryId);

    int existsActive(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    int insert(PostAdmin admin);

    int disable(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    int reactivate(@Param("categoryId") Long categoryId,
                   @Param("userId") Long userId,
                   @Param("role") String role,
                   @Param("roleId") Long roleId);

    int updateRole(@Param("categoryId") Long categoryId,
                   @Param("userId") Long userId,
                   @Param("role") String role,
                   @Param("roleId") Long roleId);

    List<Map<String, Object>> selectCandidateUsers(@Param("keyword") String keyword,
                                                   @Param("limit") int limit);
}
