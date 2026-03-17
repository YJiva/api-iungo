package com.apiiungo.mapper;

import com.apiiungo.entity.InviteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InviteRelationMapper {
    int insert(InviteRelation relation);

    /**
     * 查询所有邀请关系，附带邀请人和被邀请人的基础信息（id、username、avatar）。
     */
    java.util.List<java.util.Map<String, Object>> selectAllWithUsers();

    /**
     * 查询与指定用户直接相关的邀请关系（作为邀请人或被邀请人）。
     */
    java.util.List<java.util.Map<String, Object>> selectCloseWithUsers(@Param("userId") Long userId);
}

