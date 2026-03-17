package com.apiiungo.mapper;

import com.apiiungo.entity.Follow;
import com.apiiungo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {

    int insertFollow(Follow follow);

    int deleteFollow(@Param("userId") Long userId, @Param("targetId") Long targetId);

    int exists(@Param("userId") Long userId, @Param("targetId") Long targetId);

    List<User> selectFollowing(@Param("userId") Long userId);

    List<User> selectFollowers(@Param("userId") Long userId);
}

