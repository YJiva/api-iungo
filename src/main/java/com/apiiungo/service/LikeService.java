package com.apiiungo.service;

public interface LikeService {
    boolean toggleLike(Long userId, Long targetType, Long targetId);
    boolean isLiked(Long userId, Long targetType, Long targetId);
    int countLikes(Long targetType, Long targetId);
    int deleteByTarget(Long targetType, Long targetId);
}
