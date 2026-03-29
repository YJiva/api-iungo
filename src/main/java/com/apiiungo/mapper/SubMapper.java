package com.apiiungo.mapper;

import com.apiiungo.entity.Sub;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubMapper {

    int insert(Sub sub);

    List<Sub> selectByUser(@Param("userId") Long userId);

    int markRead(@Param("id") Long id, @Param("userId") Long userId);

    int markAllRead(@Param("userId") Long userId);

    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    int deleteAll(@Param("userId") Long userId);

    int deleteRead(@Param("userId") Long userId);
}

