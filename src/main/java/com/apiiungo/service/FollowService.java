package com.apiiungo.service;

import com.apiiungo.entity.User;

import java.util.List;

public interface FollowService {

    boolean toggleFollow(Long userId, Long targetId);

    boolean isFollowing(Long userId, Long targetId);

    List<User> listFollowing(Long userId);

    List<User> listFollowers(Long userId);
}

