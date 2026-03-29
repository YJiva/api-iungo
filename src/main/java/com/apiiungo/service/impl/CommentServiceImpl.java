package com.apiiungo.service.impl;

import com.apiiungo.entity.Comment;
import com.apiiungo.mapper.CommentMapper;
import com.apiiungo.service.CommentService;
import com.apiiungo.util.TimestampId;
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
        if (comment.getId() == null) {
            comment.setId(TimestampId.next());
        }
        comment.setCreateTime(LocalDateTime.now());
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0);
        }
        commentMapper.insertComment(comment);
        return comment;
    }

    @Override
    public List<Comment> listByTarget(Long targetType, Long targetId) {
        return commentMapper.selectByTarget(targetType, targetId);
    }

    @Override
    public Comment getById(Long id) {
        if (id == null) return null;
        return commentMapper.selectById(id);
    }

    @Override
    public boolean updateLikeCount(Long id, int delta) {
        if (id == null || delta == 0) return false;
        return commentMapper.updateLikeCount(id, delta) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) return false;
        return commentMapper.deleteById(id) > 0;
    }
}
