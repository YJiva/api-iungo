package com.apiiungo.service;

import com.apiiungo.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment);
    List<Comment> listByPost(Long postId);
}
