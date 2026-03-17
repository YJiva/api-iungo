package com.apiiungo.service.impl;

import com.apiiungo.entity.Comment;
import com.apiiungo.mapper.CommentMapper;
import com.apiiungo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Comment addComment(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insertComment(comment);
        return comment;
    }

    @Override
    public List<Comment> listByPost(Long postId) {
        return commentMapper.selectByPost(postId);
    }
}
