package com.apiiungo.mapper;

import com.apiiungo.entity.Collection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CollectionMapper {

    int insert(Collection collection);

    int delete(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    int exists(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    List<Collection> selectByUser(@Param("userId") Long userId);
}

