package com.apiiungo.service;

public interface CommentLikeService {
    boolean toggleLike(Long userId, Long commentId);
    boolean isLiked(Long userId, Long commentId);
    int countLikes(Long commentId);
    void deleteLikesByComment(Long commentId);
}
