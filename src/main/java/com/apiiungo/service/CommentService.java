package com.apiiungo.service;

import com.apiiungo.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment);

    List<Comment> listByTarget(Long targetType, Long targetId);

    Comment getById(Long id);

    boolean updateLikeCount(Long id, int delta);

    boolean deleteById(Long id);
}
