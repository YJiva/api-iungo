package com.apiiungo.mapper;

import com.apiiungo.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {
    int insertFavorite(Favorite fav);

    int deleteFavorite(@Param("userId") Long userId,
                       @Param("targetType") Long targetType,
                       @Param("targetId") Long targetId);

    int countByTarget(@Param("targetType") Long targetType,
                      @Param("targetId") Long targetId);

    int exists(@Param("userId") Long userId,
               @Param("targetType") Long targetType,
               @Param("targetId") Long targetId);
}
