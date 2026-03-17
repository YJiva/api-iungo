package com.apiiungo.mapper;

import com.apiiungo.entity.InviteCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InviteCodeMapper {
    InviteCode selectByCode(@Param("code") String code);
    InviteCode selectByUserId(@Param("userId") Long userId);
    int insert(InviteCode inviteCode);
}
