package com.apiiungo.mapper;

import com.apiiungo.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {
    int insertLike(Like like);

    int deleteLike(@Param("userId") Long userId,
                   @Param("targetType") Long targetType,
                   @Param("targetId") Long targetId,
                   @Param("action") String action);

    int exists(@Param("userId") Long userId,
               @Param("targetType") Long targetType,
               @Param("targetId") Long targetId,
               @Param("action") String action);

    int countByTarget(@Param("targetType") Long targetType,
                      @Param("targetId") Long targetId,
                      @Param("action") String action);

    int deleteByTarget(@Param("targetType") Long targetType,
                       @Param("targetId") Long targetId,
                       @Param("action") String action);
}
