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
    public boolean toggleFavorite(Long postId, Long userId) {
        int exists = favoriteMapper.exists(postId, userId);
        if (exists > 0) {
            favoriteMapper.deleteFavorite(postId, userId);
            return false;
        } else {
            com.apiiungo.entity.Favorite fav = new com.apiiungo.entity.Favorite();
            fav.setPostId(postId);
            fav.setUserId(userId);
            fav.setCreateTime(LocalDateTime.now());
            favoriteMapper.insertFavorite(fav);
            return true;
        }
    }

    @Override
    public boolean isFavorited(Long postId, Long userId) {
        return favoriteMapper.exists(postId, userId) > 0;
    }

    @Override
    public int countFavorites(Long postId) {
        return favoriteMapper.countByPost(postId);
    }
}
