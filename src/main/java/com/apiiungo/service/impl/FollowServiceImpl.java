package com.apiiungo.service.impl;

import com.apiiungo.entity.Follow;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.FollowMapper;
import com.apiiungo.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Override
    public boolean toggleFollow(Long userId, Long targetId) {
        if (userId == null || targetId == null || userId.equals(targetId)) {
            return false;
        }
        int exists = followMapper.exists(userId, targetId);
        if (exists > 0) {
            followMapper.deleteFollow(userId, targetId);
            return false;
        } else {
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setTargetId(targetId);
            follow.setCreateTime(LocalDateTime.now());
            followMapper.insertFollow(follow);
            return true;
        }
    }

    @Override
    public boolean isFollowing(Long userId, Long targetId) {
        if (userId == null || targetId == null) {
            return false;
        }
        return followMapper.exists(userId, targetId) > 0;
    }

    @Override
    public List<User> listFollowing(Long userId) {
        return followMapper.selectFollowing(userId);
    }

    @Override
    public List<User> listFollowers(Long userId) {
        return followMapper.selectFollowers(userId);
    }
}

