package com.apiiungo.mapper;

import com.apiiungo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    int insertComment(Comment comment);

    List<Comment> selectByTarget(@Param("targetType") Long targetType, @Param("targetId") Long targetId);

    Comment selectById(@Param("id") Long id);

    int updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    int deleteById(@Param("id") Long id);
}
