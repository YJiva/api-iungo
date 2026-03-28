package com.apiiungo.service;

public interface FavoriteService {
    boolean toggleFavorite(Long userId, Long targetType, Long targetId);
    boolean isFavorited(Long userId, Long targetType, Long targetId);
    int countFavorites(Long targetType, Long targetId);
}
