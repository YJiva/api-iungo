package com.apiiungo.service;

public interface FavoriteService {
    boolean toggleFavorite(Long postId, Long userId);
    boolean isFavorited(Long postId, Long userId);
    int countFavorites(Long postId);
}
