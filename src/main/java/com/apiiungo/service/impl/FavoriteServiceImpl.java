package com.apiiungo.service.impl;

import com.apiiungo.mapper.FavoriteMapper;
import com.apiiungo.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean toggleFavorite(Long userId, Long targetType, Long targetId) {
        int exists = favoriteMapper.exists(userId, targetType, targetId);
        if (exists > 0) {
            favoriteMapper.deleteFavorite(userId, targetType, targetId);
            return false;
        }
        com.apiiungo.entity.Favorite fav = new com.apiiungo.entity.Favorite();
        fav.setUserId(userId);
        fav.setTargetType(targetType);
        fav.setTargetId(targetId);
        fav.setCreateTime(LocalDateTime.now());
        favoriteMapper.insertFavorite(fav);
        return true;
    }

    @Override
    public boolean isFavorited(Long userId, Long targetType, Long targetId) {
        return favoriteMapper.exists(userId, targetType, targetId) > 0;
    }

    @Override
    public int countFavorites(Long targetType, Long targetId) {
        return favoriteMapper.countByTarget(targetType, targetId);
    }
}
