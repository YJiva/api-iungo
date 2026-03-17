package com.apiiungo.mapper;

import com.apiiungo.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {
    int insertFavorite(Favorite fav);
    int deleteFavorite(@Param("postId") Long postId, @Param("userId") Long userId);
    int countByPost(@Param("postId") Long postId);
    int exists(@Param("postId") Long postId, @Param("userId") Long userId);
}
