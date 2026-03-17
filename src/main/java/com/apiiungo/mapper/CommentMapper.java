package com.apiiungo.mapper;

import com.apiiungo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int insertComment(Comment comment);
    List<Comment> selectByPost(@Param("postId") Long postId);
}
