package com.apiiungo.service;

import com.apiiungo.entity.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    // 账号密码登录
    User loginByPassword(String username, String password);
    // 根据用户名查询（不校验密码）
    User findByUsername(String username);
    // 邮箱验证码登录
    User loginByEmailCode(String email, String code);
    // 邀请注册（校验邀请码+创建用户+绑定关系）
    boolean inviteRegister(User user, String inviteCode);
    // 生成专属邀请码
    String generateInviteCode(Long userId);
    // 查询用户邀请树（全局）
    List<Map<String, Object>> getInviteTree(Long userId);
    // 查询与当前用户关系更近的邀请关系
    List<Map<String, Object>> getInviteTreeClose(Long userId);
    // 修改密码
    boolean updatePassword(Long userId, String oldPwd, String newPwd);
    // 编辑个人资料
    boolean updateProfile(User user);

    // 根据邮箱查询用户
    User findByEmail(String email);

    // 根据ID查询用户
    User findById(Long id);
}