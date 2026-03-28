package com.apiiungo.service.impl;

import com.apiiungo.entity.Like;
import com.apiiungo.mapper.LikeMapper;
import com.apiiungo.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikeServiceImpl implements LikeService {

    private static final String ACTION_LIKE = "LIKE";

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public boolean toggleLike(Long userId, Long targetType, Long targetId) {
        int exists = likeMapper.exists(userId, targetType, targetId, ACTION_LIKE);
        if (exists > 0) {
            likeMapper.deleteLike(userId, targetType, targetId, ACTION_LIKE);
            return false;
        }
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(targetType);
        like.setTargetId(targetId);
        like.setAction(ACTION_LIKE);
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insertLike(like);
        return true;
    }

    @Override
    public boolean isLiked(Long userId, Long targetType, Long targetId) {
        return likeMapper.exists(userId, targetType, targetId, ACTION_LIKE) > 0;
    }

    @Override
    public int countLikes(Long targetType, Long targetId) {
        return likeMapper.countByTarget(targetType, targetId, ACTION_LIKE);
    }

    @Override
    public int deleteByTarget(Long targetType, Long targetId) {
        return likeMapper.deleteByTarget(targetType, targetId, ACTION_LIKE);
    }
}
