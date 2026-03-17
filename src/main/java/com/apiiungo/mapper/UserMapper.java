package com.apiiungo.mapper;

import com.apiiungo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {
    // 账号查询用户
    User selectByUsername(String username);
    // 根据ID查询用户
    User selectById(Long id);
    // 邮箱查询用户
    User selectByEmail(String email);
    // 邀请码查询用户（找邀请人）
    User selectByInviteCode(String inviteCode);
    // 新增用户
    int insertUser(User user);
    // 更新用户信息
    int updateUser(User user);
    // 绑定邀请关系（更新被邀请人的inviterId）
    int bindInviter(@Param("userId") Long userId, @Param("inviterId") Long inviterId);
    // 递归查询邀请树（核心：查某用户的所有下级）
    List<Map<String, Object>> selectInviteTree(@Param("parentId") Long parentId);

    // 查询所有用户（仅用于首页测试展示）
    List<User> selectAll();
}